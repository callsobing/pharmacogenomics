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
import org.apache.spark.api.java.function.Function;

import java.io.*;
import java.util.*;

public class VcfReaderYinHung implements Serializable {
    public static void main(String[] args) throws IOException {
        final String vcfPath = args[0];
        final String chr = args[1];
        final Integer start = Integer.parseInt(args[2]);
        final Integer end = Integer.parseInt(args[3]);
        final String outputPath = args[4];
        SparkConf conf = new SparkConf().setAppName("allele-freq-calculator");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> file = sc.textFile(vcfPath).repartition(1500);
        final String VCFHeaderStrings = file
                .filter(line -> line.startsWith("#"))
                .reduce((s1, s2) -> s1 + "\n" + s2);

        JavaRDD<String> lines = file.filter(line -> !line.startsWith("#"));

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

        List<String> output =

                vctx.filter(new Function<VariantContext, Boolean>() {
            @Override
            public Boolean call(VariantContext innerVctx) throws Exception {
                return innerVctx.getContig().equals(chr) &&
                        innerVctx.getStart() > start && innerVctx.getStart() < end &&
                        innerVctx.getAlternateAllele(0).getBaseString().length() == 1 &&
                        innerVctx.getReference().getBaseString().length() == 1 &&
                        innerVctx.getAlternateAlleles().size() == 1;
            }
        })


                        .map(new Function<VariantContext, String>() {
            @Override
            public String call(VariantContext vctx) throws Exception {
                String baseString = vctx.getContig().concat("\t")
                        .concat(String.valueOf(vctx.getStart())).concat("\t")
                        .concat(String.valueOf(vctx.getStart())).concat("\t")
                        .concat(vctx.getReference().getBaseString()).concat(">")
                        .concat(vctx.getAlternateAllele(0).getBaseString().concat("\t"));
                String finalString = baseString.concat(vctx.getAttribute("AMR_AF").toString()).concat("\t").concat("AMR").concat("\n")
                        .concat(baseString.concat(vctx.getAttribute("AFR_AF").toString()).concat("\t").concat("AFR").concat("\n"))
                        .concat(baseString.concat(vctx.getAttribute("EUR_AF").toString()).concat("\t").concat("EUR").concat("\n"))
                        .concat(baseString.concat(vctx.getAttribute("SAS_AF").toString()).concat("\t").concat("SAS").concat("\n"))
                        .concat(baseString.concat(vctx.getAttribute("EAS_AF").toString()).concat("\t").concat("EAS").concat("\n"));
                return finalString;
            }
        }).collect();

        FileWriter out = new FileWriter(outputPath);
        for(String data: output){
            out.write(data);
        }
        // AMR, AFR, EUR

    }

    public static void calculateAlleleFreq (String altAlleleString, VariantContext vctx, String chr, Integer start){
        String key = chr.concat("_").concat(start.toString()).concat("_").concat(altAlleleString);
        Allele altAllele = vctx.getAllele(altAlleleString);
        float altCount = vctx.getCalledChrCount(altAllele);

        System.out.println("The request Allele " + key + " has allele freq = " + altCount/992);
    }
}
