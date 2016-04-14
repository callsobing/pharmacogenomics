package C4lab;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;  //e1
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;  //e2
import java.util.Set;   //e3


public class VcfReaderChaoi
{
//    public static List<String> rsIDs = Arrays.asList(
//            "rs587638290",
//            "rs587736341",
//            "rs534965407",
//            "rs9609649",
//            "rs5845047",
//            "rs80690",
//            "rs137506",
//            "rs138355780"
//    );



    public static void main( String[] args ) throws IOException {
        VCFCodec vcfCodec = new VCFCodec();
       // final String vcfPath = "/Users/yat/vcf/TSC/merged_351_354_356.vcf";
        final String vcfPath = "C:/Users/user/Idealproject/c4lab/src/main/java/C4lab/merged_351_354_356.vcf";
//        final String vcfPath = args[0];
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));

        String line;
        String headerLine = "";
        VariantContext vctx;
        int count=0;

        while ((line = schemaReader.readLine()) != null) {
            if(line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if(!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                //ctrl +/ command all
//                if(vctx.getAlternateAlleles().get(0).length()>100)
//                    System.out.println(" length: "+vctx.getAlternateAlleles().get(0).length()+" ref: "+vctx.getReference() +
//                            " alt:" + vctx.getAlternateAlleles().get(0) + " GT: " +
//                            vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString() );
                Set<String> sampleNames = vctx.getSampleNames();
                List<String> sampleNamesList = new ArrayList<String>(sampleNames);
                List<String> subCase = new ArrayList<String>(sampleNamesList.subList(0, 9));
                List<String> subControl = new ArrayList<String>(sampleNamesList.subList(10, 19));

                // alleleNumbers  for every line of vctx count altAlleles #
                int alleleNumbers = vctx.getAlternateAlleles().size();
                for (  int  i=0; i<alleleNumbers ;i++){
                    String altAllele = vctx.getAlternateAllele(i).getBaseString(); // A or T or ATTC....
                    if(equalitySampleAllele(vctx, subCase, altAllele)==10 && (equalitySampleAllele(vctx, subControl, altAllele)==0)){
                        count++;
                    }
                }
                System.out.println("number of variants that satisfy the case-control condition : "+ count);

//
//                String ID = "rs71252250";
//                Set<String> sampleNames = vctx.getSampleNames();
//                //System.out.println(sampleNames);
//                String refAllele = vctx.getReference().getDisplayString();// getDisplayString  why Not use list<Allele>
//               // System.out.println(vctx.getReference());
//                List<Allele> altAlleles = vctx.getAlternateAlleles();
//               // System.out.println(altAlleles.size());
//                for(Allele altAllele: altAlleles) {
//                    calculateAltAlleleFrequency(vctx, ID, sampleNames, altAllele.getBaseString()); //getBaseString return ref string
//                    System.out.println(altAllele.getBaseString());
   //             }
            }
        }
    }

//    public static double calculateAltAlleleFrequency(VariantContext vctx, String ID, Set<String> sampleNames, String altSeq) {
//
//        int counter = 0;
//        double AF = 0.0;
//
//        if (vctx.getID().equals(ID)) { //can filter all the selected rsID
//            for(String sampleName: sampleNames){
//                String genotypeString = vctx.getGenotype(sampleName).getGenotypeString();
//              //  System.out.println(vctx.getGenotype(sampleName)); getGenotype return an object,
//               //System.out.println(genotypeString);// return the genotype
//                String [] alleles = genotypeString.split("/"); // for every sample its genotype string is store in alleles array
//              //  System.out.println(alleles);
//                String allele1 = alleles[0];
//                String allele2 = alleles[1];
//
//               // System.out.println(allele1 + " @@@@ " + allele2); // Just print for debug
//                if (allele1.equals(altSeq)) {
//                    counter++;
//                }
//                if (allele2.equals(altSeq)) {
//                    counter++;
//                }
//            }
//        }
//        AF = counter / vctx.getCalledChrCount();
//        return AF;
//    }

    public static int equalitySampleAllele(VariantContext vctx, List<String> samples, String altAllele){
        int counter=0;
        for (String sample : samples) {
            String genotypeString = vctx.getGenotype(sample).getGenotypeString();
            String[] alleles = genotypeString.split("/"); // for every sample its genotype string is store in alleles array
            //  System.out.println(alleles);
            String allele1 = alleles[0];
            String allele2 = alleles[1];

            if (allele1.equals(altAllele) || allele2.equals(altAllele)) {
                counter++;
            }
        }
        return counter;
    }
    // test1
    //test2
}
