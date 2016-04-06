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
            "s9609649",
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
            int alleleNumbers = vctx.getAlternateAlleles().size();

            if(alleleNumbers == 1) {
                String rsId = vctx.getID();
                calculateAlleleFreq(0, vctx, sampleNames, rsId);
                if(checkAfGtAverageSingle(vctx)) rsIdsAboveAverage.add(rsId);
                continue;
            }

            String [] rsIdList = vctx.getID().split(";");
            for (int i = 0; i < alleleNumbers; i++) {
                if(rsIdList.length != alleleNumbers) continue;

                String rsId = rsIdList[i];
                calculateAlleleFreq(i, vctx, sampleNames, rsId);
                if(checkAfGtAverageMulti(i, vctx)) rsIdsAboveAverage.add(rsId);
            }
        }
        System.out.println("# of vairants with SAS, EAS AF larger than average AF: " + rsIdsAboveAverage.size());
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
