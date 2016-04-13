package C4lab;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import java.io.*;
import java.util.List;
import java.util.Set;


public class VcfReaderChaoi
{
    public static void main( String[] args ) throws IOException {
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = "/Users/yat/vcf/TSC/merged_351_354_356.vcf";
//        final String vcfPath = "D:/lab/A0087Y_09182015_bwamem.filtered.haplotype.SnpIndel.vcf";
//        final String vcfPath = args[0];
        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));

        // vcfPath to folder
        String line;
        String headerLine = "";
        VariantContext vctx;

        while ((line = schemaReader.readLine()) != null) {
            if(line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if(!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                if(vctx.getAlternateAlleles().get(0).length()>100)
                    System.out.println(" length: "+vctx.getAlternateAlleles().get(0).length()+" ref: "+vctx.getReference() +
                            " alt:" + vctx.getAlternateAlleles().get(0) + " GT: " +
                            vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString() );

//                public static double calculateAltAlleleFrequency(VariantContext vctx){
//                    final double altAlleleCount = vctx.getAttributeAsInt(VCFConstants.ALLELE_COUNT_KEY, 0);
//                            final double totalCount = vctx.getAttributeAsInt(VCFConstants.ALLELE_NUMBER_KEY, 0); 
//                            final double aa f= altAlleleCount/totalCount;
//                    return aaf;

               // String[] sampleName = vctx.getSampleNames();
                //vctx a 2 dimenstion arraylist

                String ID = "rs587638290";
                Set<String> sampleNames = vctx.getSampleNames();
                String refAllele = vctx.getReference().getDisplayString();
                List<Allele> altAlleles = vctx.getAlternateAlleles();
                for(Allele altAllele: altAlleles) {
                    calculateAltAlleleFrequency(vctx, ID, sampleNames, altAllele.getBaseString());
                }
            }
        }
    }

    public static double calculateAltAlleleFrequency(VariantContext vctx, String ID, Set<String> sampleNames, String altSeq) {
        // vctx a list? can get each line in vctx???　vctx = vcfCodec.decode(line);
        //if vctx is an arraylist, ? list.get(i)
        int counter = 0;
        double AF = 0.0;

        if (vctx.getID().equals(ID)) { //can filter all the selected rsID
            for(String sampleName: sampleNames){
                String genotypeString = vctx.getGenotype(sampleName).getGenotypeString();
                String [] alleles = genotypeString.split("/");
                String allele1 = alleles[0];
                String allele2 = alleles[1];

                System.out.println(allele1 + " @@@@ " + allele2); // Just print for debug
                if (allele1.equals(altSeq)) {
                    counter++;
                }
                if (allele2.equals(altSeq)) {
                    counter++;
                }
            }
        }
        AF = counter / vctx.getCalledChrCount();
        return AF;
    }
    // test1
    //test2
}
