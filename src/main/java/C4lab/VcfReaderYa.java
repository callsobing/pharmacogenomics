package C4lab;

import function.CountGetter;
import function.MergeCounts;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.*;
import java.util.*;

public class VcfReaderYa implements Serializable {
    final static Integer ITERATIONS = 10000;

    public static List<String> rsIDs = Arrays.asList(
            "rs587638290",
            "rs587736341",
            "rs534965407",
            "rs9609649",
            "rs5845047",
            "rs80690",
            "rs137506",
            "rs138355780"
    );

    public static void main(String[] args) throws IOException {
        final String vcfPath = args[0];
        SparkConf conf = new SparkConf().setAppName("yat-test");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> file = sc.textFile(vcfPath);
        final String VCFHeaderStrings = file
                .filter(line -> line.startsWith("#"))
                .reduce((s1, s2) -> s1 + "\n" + s2);

        JavaRDD<String> lines = file.filter(line -> !line.startsWith("#"));

        List<List<Integer>> caseSampleNames = new ArrayList<List<Integer>>();
        List<List<Integer>> controlSampleNames = new ArrayList<List<Integer>>();
        List<Integer> countList = new ArrayList<Integer>();

        JavaRDD<VariantContext> vctx = lines.mapPartitions(
                line -> {
                    final VCFCodec codec = new VCFCodec();
                    codec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                            new StringReader(VCFHeaderStrings), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

                    List<VariantContext> col = new ArrayList<>();
                    line.forEachRemaining(s -> col.add(codec.decode(s)));

                    return col;
                }
        );

        createRandomSampleSets(vctx.first(), caseSampleNames, controlSampleNames, countList);

        List<Integer> tt = vctx.map(new CountGetter(caseSampleNames, controlSampleNames)).reduce(new MergeCounts());

        System.out.println("######## Allele number with allele presences in all cases but no controls:");
        for(int k = 0; k < 10; k++) {
            System.out.println(tt.get(k));
        }
    }

    public static void createRandomSampleSets (VariantContext vctx, List<List<Integer>> caseSampleNames, List<List<Integer>> controlSampleNames, List<Integer> countList){
        for(int i = 1; i <= ITERATIONS; i++) {
            countList.add(i-1, 0);
            List<Integer> innerCaseIdxList = new ArrayList<Integer>();
            List<Integer> innerControlIdxList = new ArrayList<Integer>();
            List<Integer> innerCaseIdx = new ArrayList<Integer>();
            List<Integer> innerControlIdx = new ArrayList<Integer>();
            List<String> sampleNames = vctx.getSampleNamesOrderedByName();

            while(innerCaseIdxList.size() < 5) {
                int random = (int)(Math.random() * sampleNames.size()); // idx from 0 ~ size-1
                if(innerCaseIdx.contains(random)) continue;
                innerCaseIdxList.add(random);
                innerCaseIdx.add(random);
            }
            caseSampleNames.add(innerCaseIdxList);

            while(innerControlIdxList.size() < 5) {
                int random = (int)(Math.random() * sampleNames.size()); // idx from 0 ~ size-1
                if(innerCaseIdx.contains(random) || innerControlIdx.contains(random)) continue;
                innerControlIdxList.add(random);
                innerControlIdx.add(random);
            }
            controlSampleNames.add(innerControlIdxList);
        }
    }

    public static boolean checkAfGtAverageSingle (VariantContext vctx){
        float easAf = Float.parseFloat(vctx.getAttribute("EAS_AF").toString());
        float sasAf = Float.parseFloat(vctx.getAttribute("SAS_AF").toString());
        float af = Float.parseFloat(vctx.getAttribute("AF").toString());
        return easAf > af && sasAf > af;
    }

    public static boolean checkAfGtAverageMulti (Integer altAlleleIdx, VariantContext vctx){
        float easAf = Float.parseFloat(((List<String>)vctx.getAttribute("EAS_AF")).get(altAlleleIdx));
        float sasAf = Float.parseFloat(((List<String>)vctx.getAttribute("SAS_AF")).get(altAlleleIdx));
        float af = Float.parseFloat(((List<String>)vctx.getAttribute("AF")).get(altAlleleIdx));
        return easAf > af && sasAf > af;

    }

    public static void calculateAlleleFreq (Integer altAlleleIdx, VariantContext vctx, Set<String> sampleNames, String rsId){
        if(!rsIDs.contains(rsId)) return;
        Allele altAllele = vctx.getAlternateAllele(altAlleleIdx);
        float altCount = 0f;

        for(String sample: sampleNames){
             altCount += vctx.getGenotype(sample).countAllele(altAllele);
        }
        System.out.println(rsId + " has AF of " + altCount/vctx.getCalledChrCount());
    }
}
