package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;

import javax.lang.model.type.NullType;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class VcfReaderJunliang
{
    public static void main( String[] args ) throws IOException {
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = args[0];

        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));

        String line;
        String headerLine = "";
        VariantContext vctx;
        //int SampleN=0;
        //String[] rsid={"rs587697622","rs587638290","rs587736341","rs534965407","rs9609649","rs5845047","rs80690","rs137506","rs138355780"};
        String[] cases =new String[10];
        Set <String> control = new HashSet<String>();

        while ((line = schemaReader.readLine()) != null) {
            if(line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if(!line.startsWith("#")) {

                vctx = vcfCodec.decode(line);
                String[] SampleId = vctx.getSampleNames().toString().split(",");
                for (int i = 0; i < 10; i++) {
                    if (i < 10)
                        cases[i] = SampleId[i];
                    else
                        control.add(SampleId[i]);
                }
                System.out.println(cases[5]+vctx.getGenotypes(cases[5]));

                printSampleGenotypes(vctx);

            }
        }
    }

    public static void printSampleGenotypes(VariantContext vctx){
        Set<String> SampleId = vctx.getSampleNames();
        for(String samplename: SampleId) {
            System.out.println(samplename + vctx.getGenotypes("NA18525"));
        }
    }
}

