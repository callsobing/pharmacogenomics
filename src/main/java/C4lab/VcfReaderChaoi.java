package C4lab;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;  //e1
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import java.io.*;
import java.util.*;



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
//    );sta
    static int iteration = 10000;

    public static void main( String[] args ) throws IOException {
        long time1 ,time2;
        time1 = System.currentTimeMillis();
        VCFCodec vcfCodec = new VCFCodec();
        FileWriter out = new FileWriter("output1.txt");

        // final String vcfPath ="C:/Users/user/Idealproject/c4lab/src/main/java/C4lab/ALL.chr22.first1000Lines.vcf";
        //final String vcfPath = "C:/Users/user/Idealproject/c4lab/src/main/java/C4lab/merged_351_354_356.vcf";
        final String vcfPath = args[0];

        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));

        String line;
        String headerLine = "";
        VariantContext vctx;
        int count[] = new int[iteration];
        int statistic[] =new int[iteration];
        boolean checkSubListDone= false;
        List<String> subCase = new ArrayList<String>();
        List<String> subControl = new ArrayList<String>();
        List<String> sampleNamesOrder = new ArrayList();
        List<String> totalCaseControl = new ArrayList<String>();
       // List<Integer> countList = new ArrayList<Integer>();


        while ((line = schemaReader.readLine()) != null) {

            if (line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if (!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                // initialization  only once
                if (!checkSubListDone) {
                    sampleNamesOrder = vctx.getSampleNamesOrderedByName();
                    System.out.println(sampleNamesOrder);
                    totalCaseControl = randomSubSamples(vctx, sampleNamesOrder);
                    //initialization of  total random case-control set

                    // cloud
//                    subCase = sampleNamesOrder.subList(0, 10);
//                    subControl = sampleNamesOrder.subList(10, 20);
                    // test
                    //  subCase = sampleNamesOrder.subList(0, 1);
                    // subControl = sampleNamesOrder.subList(1, 3);
                    checkSubListDone = true;
                }

                // System.out.println(randomSubSamples(vctx,sampleNamesOrder));
                for (int j = 0; j < iteration; j = j + 10) {
                    // alleleNumbers  for every line of vctx count altAlleles #j
                    subCase= totalCaseControl.subList(j,j+5);
                    subControl= totalCaseControl.subList(j+5,j+10);
                    int alleleNumbers = vctx.getAlternateAlleles().size(); //一行vctx 裡面有幾種ALT
                    for (int i = 0; i < alleleNumbers; i++) {
                        String altAllele = vctx.getAlternateAllele(i).getBaseString(); // A or T or ATTC....

                        if (equalitySampleAllele(vctx, subCase, altAllele) == 5 && (equalitySampleAllele(vctx, subControl, altAllele) == 0)) {
                            //   System.out.println("Matched allele id: " + vctx.getID());
                            out.write("\n"+"Matched allele id: " + vctx.getID());

                            count[j/10]++;
                        }
                    }
                    // System.out.println("number of variants that satisfy the case-control condition : "+ count);

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
        Arrays.sort(count);
        System.out.println(Arrays.toString(count));
        for (int i=0;i<iteration;i++){
            statistic[i]=(count[i]/10);
        }
        Arrays.sort(statistic);
        System.out.println("\n"+"number of variants that satisfy the case-control condition : "+ Arrays.toString(count));
        System.out.println("\n"+"statistic calculation : "+ Arrays.toString(statistic));
        out.write("\n"+"number of variants that satisfy the case-control condition : "+ Arrays.toString(count));
        out.write("\n"+"statistic calculation : "+ Arrays.toString(statistic));
        time2  = System.currentTimeMillis();
        System.out.println("\n"+"Program run time: "+(time2-time1)/1000+" sec");
        out.write("\n"+"Program run time: "+(time2-time1)/1000+" sec");
        out.close();

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
//    private static boolean checkSubListDone(VariantContext vctx, List<String> samples){
//        List<String> sampleNamesOrder = vctx.getSampleNamesOrderedByName();
//        List<String> subCase  = sampleNamesOrder.subList(0,10);
//        List<String> subControl = sampleNamesOrder.subList(10,20);
//
//    }

    private static int equalitySampleAllele(VariantContext vctx, List<String> samples, String altAllele){
        int counter=0;
        for (String sample : samples) {
            String genotypeString = vctx.getGenotype(sample).getGenotypeString();

            String[] alleles = genotypeString.split("\\|"); // for every sample its genotype string is store in alleles array ('\\|') for cloud
           if (!genotypeString.contains(".")) { // if ./. 跳過, 會部會比較快?
               if(alleles.length==2) {

                 //  System.out.println(genotypeString);

                   String allele1 = alleles[0];
                   String allele2 = alleles[1];
                   if (allele1.equals(altAllele) || allele2.equals(altAllele)) {
                       counter++;
                   }
               }
           }
        }
        return counter;
    }

    // initialization 50 pairs of control case
    private static List<String> randomSubSamples(VariantContext vctx, List<String> samples){
        List<String> randomCreate = new ArrayList<String>();
        Random randomID= new Random();
        for(int i=1; i<=iteration ;i++) {
            randomCreate.add(vctx.getSampleNamesOrderedByName().get(randomID.nextInt(samples.size())));
        }
        return randomCreate;
    }




}
