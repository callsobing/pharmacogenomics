package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import java.io.*;


public class VcfReaderYuta
{
    public static void main( String[] args ) throws IOException {
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = args[0];

        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));

        String line;
        String headerLine = "";
        VariantContext vctx;

        String[] rsIDs = {
                "rs587638290",
                "rs587736341",
                "rs534965407",
                "s9609649",
                "rs5845047",
                "rs80690",
                "rs137506",
                "rs138355780",
        };
        int rsIDsLength = rsIDs.length;
        int[] rsIDCount = new int[rsIDsLength];




            while ((line = schemaReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    headerLine = headerLine.concat(line).concat("\n");
                    continue;
                }
                vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                        new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

                for (int i=0; i<rsIDsLength; i++) {
                    //
                    if (!line.startsWith("#")) {
                        vctx = vcfCodec.decode(line);
                        if (vctx.getID().equals(rsIDs[i])) {
                            rsIDCount[i]++;
//                            System.out.println(
//                                    "|rsIDCount: " + rsIDCount[i]
//                                            + "| |rsID: " + vctx.getID()
//                                            + "| |POS: " + vctx.getEnd()
//                                            + "| |ref: " + vctx.getReference()
//                                            + "| |alt(getAlternateAlleles): " + vctx.getAlternateAlleles().get(0)
//                                            + "| |GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString()
//                                            + "|");
                        }
                    }
                }
                // print出所有有 rsID 的 variants：
//            if(!line.startsWith("#")) {
//                vctx = vcfCodec.decode(line);
//                if(!(vctx.getID()==".")) {
//                    System.out.println(
//                               "|rsID: " + vctx.getID()
//                            +"| |ref: " + vctx.getReference()
//                            +"| |alt(getAlternateAlleles): " + vctx.getAlternateAlleles().get(0)
//                            +"| |GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString()
//                            +"| |"
//                    );
//                }
//            }

                // print出所有長度大於 100 的 alt allele：
//            if (!line.startsWith("#")) {
//                vctx = vcfCodec.decode(line);
//                if (vctx.getAlternateAlleles().get(0).length() > 100)
//                    System.out.println(
//                            " |length: " + vctx.getAlternateAlleles().get(0).length() + " |" +
//                            " |ref: " + vctx.getReference() + " |" +
//                            " \n|alt:" + vctx.getAlternateAlleles().get(0) + " |" +
//                            " \n|GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString() + " |"
//                    );
//            }


                // 修改變數rsIDtarget以搜尋特定rsID
//            if(!line.startsWith("#")) {
//                vctx = vcfCodec.decode(line);
//                if(vctx.getID().equals(rsIDtarget)) {
//                    System.out.println(
//                               "|rsID: " + vctx.getID()
//                            +"| |POS: " + vctx.getEnd()
//                            +"| |ref: " + vctx.getReference()
//                            +"| |alt(getAlternateAlleles): " + vctx.getAlternateAlleles().get(0)
//                            +"| |GT: " + vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString()
//                            +"|"
//                    );
//                }
//            }
            }

        for (int i=0; i<rsIDsLength;i++){
            System.out.println("Allele Frequency of " + rsIDs[i] + ": " + rsIDCount[i]/5008.0);
        }

//        System.out.println(searchWithRSID("rs71507461"));

    }

    public static String searchWithRSID(String rsIDtarget){
        return rsIDtarget.concat(" inputted");
    }

}

