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
        final String vcfPath = "/Users/zhouyuda/Desktop/c4lab/c4Lab_pharmacogenomics/A0087Y_09182015_bwamem.filtered.haplotype.SnpIndel.vcf";

        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));

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


            // print出所有variants
//            if(!line.startsWith("#")) {
//                vctx = vcfCodec.decode(line);
//                System.out.println("ref: "+vctx.getReference() + " alt:" + vctx.getAlternateAlleles().get(0) + " GT: " +
//                        vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString() );
//            }


            // print出所有有rsID的variants
            if(!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                if(!(vctx.getID()==".")) {
                    System.out.println("rsID: " + vctx.getID() + "\t\t ref: " + vctx.getReference() + " alt:" + vctx.getAlternateAlleles().get(0) + " GT: " +
                            vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString());
                }
            }
        }
    }
}
