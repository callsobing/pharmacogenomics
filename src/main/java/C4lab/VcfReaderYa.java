package C4lab;

import htsjdk.samtools.util.Histogram;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import java.io.*;
import java.util.*;
import java.util.function.BooleanSupplier;


public class VcfReaderYa
{
    final static Integer ITERATIONS = 5000;
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

    public static void main( String[] args ) throws IOException {
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = args[0]; //"/Users/yat/vcf/TSC/merged_351_354_356.vcf";
        boolean firstDecode = true;
        boolean firstContent = true;
        VariantContext vctx;
        String line;
        String headerLine = "";
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        List<List<Integer>> caseSampleNames = new ArrayList<List<Integer>>();
        List<List<Integer>> controlSampleNames = new ArrayList<List<Integer>>();
        List<Integer> countList = new ArrayList<Integer>();

        while ((line = schemaReader.readLine()) != null) {
            if(line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            if(firstDecode){
                vcfDecoder(headerLine, vcfCodec);
                firstDecode = false;
            }

            vctx = vcfCodec.decode(line);

            if(firstContent) {
                createRandomSampleSets(vctx, caseSampleNames, controlSampleNames, countList);
                firstContent = false;
            }

            for(Allele allele: vctx.getAlternateAlleles()) {
                ArrayList<Boolean> sampleContainsAllele = new ArrayList<Boolean>();
                for (int k = 0; k < ITERATIONS; k++) {
                    if (k == 0) {
                        for (String sample : vctx.getSampleNamesOrderedByName()) {
                            sampleContainsAllele.add(vctx.getGenotype(sample).countAllele(allele) > 0); // Pre-compute
                        }
                    }
                    if (checkAlleleNotPresentInAllControl(controlSampleNames.get(k), sampleContainsAllele) &&
                            checkAllelePresentInAllCase(caseSampleNames.get(k), sampleContainsAllele)) {
                        int kk = countList.get(k);
                        countList.set(k, kk + 1);
                    }
                }
            }
        }

        System.out.println("######## Allele number with allele presences in all cases but no controls:");
        for(int k = 0; k < ITERATIONS; k++) {
            System.out.println(countList.get(k));
        }
    }

    public static void vcfDecoder (String headerLine, VCFCodec vcfCodec){
        vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));
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

    public static boolean checkAlleleNotPresentInAllControl(List<Integer> controlSamples, ArrayList<Boolean> sampleContainsAllele){
        for(Integer sampleIdx: controlSamples){
            if(sampleContainsAllele.get(sampleIdx)){
                return false;
            }
        }
        return true;
    }

    public static boolean checkAllelePresentInAllCase(List<Integer> caseSamples, ArrayList<Boolean> sampleContainsAllele){
        for(Integer sampleIdx: caseSamples){
            if(!sampleContainsAllele.get(sampleIdx)){
                return false;
            }
        }
        return true;
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
