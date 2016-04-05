package C4lab;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import java.io.*;


public class VcfReader
{
    public static void main( String[] args ) throws IOException {
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = "C:/Users/user/Idealproject/c4lab/src/main/java/C4lab/vfctest.vcf";
       // final String vcfPath = args[0];
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
                String ID = rs587638290;
                Arraylist<string> sampleNames = vctx.getSampleNames();
                public static double calculateAltAlleleFrequency( VariantContext vctx, String ID){
                 // vctx a list? can get each line in vctx???　vctx = vcfCodec.decode(line);

                        static int counter=0;
                        static double AF=0;
                        if (vctx.getID().equals(ID) ) //can filter all the selected rsID
                                for( i=0;i<=sampleName.size();i++)
                                {
                                    //list .size()
                                    if(  !int(vctx.getGenotype(sampleName.get(i)).getGenotypeString()).equals(0))
                                     counter ++;
                                }
                            AF = counter / vctx.getCalledChromosome();
                    return AF;
                }

            }
        }
    }

}
