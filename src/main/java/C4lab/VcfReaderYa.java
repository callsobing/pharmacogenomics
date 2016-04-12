package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import java.io.*;
import java.util.*;


public class VcfReaderYa
{
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
        VariantContext vctx;
        String line;
        String headerLine = "";
        Set<String> rsIdsAboveAverage = new HashSet<String>();
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));
        Integer allelesInAllCasesNoControl = 0;

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
            Set<String> sampleNames = vctx.getSampleNames();
            List<String> sampleNameList = new ArrayList<String>();
            sampleNameList.addAll(sampleNames);
            Set<String> caseSampleName = new HashSet<String>(sampleNameList.subList(0,9));
            Set<String> controlSampleName = new HashSet<String>(sampleNameList.subList(10,19));
            int alleleNumbers = vctx.getAlternateAlleles().size();

            if(alleleNumbers == 1) {
                Allele allele = vctx.getAlternateAllele(0);
                if(checkAlleleNotPresenceInAllControl(controlSampleName, vctx, allele) &&
                        checkAllelePresenceInAllCase(caseSampleName, vctx, allele)) {
                    allelesInAllCasesNoControl += 1;
                    System.out.println("Matched allele id: " + vctx.getID());
                }
                continue;
            }
            String [] rsIdList = vctx.getID().split(";");
            for (int i = 0; i < alleleNumbers; i++) {
                Allele allele = vctx.getAlternateAllele(i);
                if(checkAlleleNotPresenceInAllControl(controlSampleName, vctx, allele) &&
                        checkAllelePresenceInAllCase(caseSampleName, vctx, allele)) allelesInAllCasesNoControl += 1;
            }
        }
        System.out.println("######## Allele number with allele presences in all cases but no controls: " + allelesInAllCasesNoControl);
    }

    public static void vcfDecoder (String headerLine, VCFCodec vcfCodec){
        vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));
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

    public static boolean checkAlleleNotPresenceInAllControl (Set<String> controlSamples, VariantContext vctx, Allele allele){
        return 0 == vctx.getCalledChrCount(allele, controlSamples);
    }

    public static boolean checkAllelePresenceInAllCase (Set<String> caseSamples, VariantContext vctx, Allele allele){
        boolean allCaseSampleContains = true;
        for(String sample: caseSamples){
            allCaseSampleContains = 0 < vctx.getGenotype(sample).countAllele(allele);
        }
        return allCaseSampleContains;
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
