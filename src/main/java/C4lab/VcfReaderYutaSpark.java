package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.*;
import java.util.*;

public class VcfReaderYutaSpark implements Serializable {
    public static void main(String[] args) throws IOException {

        final boolean multiAFs = true;

        long startTimems = System.currentTimeMillis();
        System.out.println("*** PROGRAM START ***");
        final String vcfPath = args[0];

        // TODO:你的rsID list
        final List<String> rsIDs = Arrays.asList(

                "rs1594",       // chr2

                "rs2231142",    // chr4

                "rs2844665",    // chr6
                "rs3094188",    // chr6
                "rs3130501",    // chr6
                "rs3130931",    // chr6
                "rs3815087",    // chr6
                "rs9469003"    // chr6

        );
//        String[] AFprintingSequence

        // TODO:改成你們各自的名字
        SparkConf conf = new SparkConf().setAppName("Yuta_allele-freq-calculator");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> file = sc.textFile(vcfPath);
        final String VCFHeaderStrings = file
                .filter(line -> line.startsWith("#"))
                .reduce((s1, s2) -> s1 + "\n" + s2);

        JavaRDD<String> lines = file.filter(line -> !line.startsWith("#"));

        JavaRDD<VariantContext> vctx = lines.mapPartitions(
                line -> {
                    final VCFCodec codec = new VCFCodec();
                    codec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                            new StringReader(VCFHeaderStrings), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

                    List<VariantContext> col = new ArrayList<>();
                    line.forEachRemaining(s -> col.add(codec.decode(s)));

                    return col;
                }
        );

        //List<Pair> outputAsPairs =
                vctx.filter(new Function<VariantContext, Boolean>() {   /* 問題：為何是 Function<VariantContext, Boolean>() */
                    @Override
                    public Boolean call(VariantContext innerVctx) throws Exception {
                        return rsIDs.contains(innerVctx.getID());
                    }
                }).map(new Function<VariantContext, Pair>() {
                    @Override
                    public Pair call(VariantContext vctx) throws Exception {
                        String rsid = vctx.getID();
                        Double alleleFreq = vctx.getAttributeAsDouble("AF", -999.0);


                        //手算AF：
//                        float total = vctx.getCalledChrCount();    //5008
//                        float count = 0.0f;
//                        Allele alt = vctx.getAlternateAllele(0);    // [G]
//                        Set<String> sampleNames = vctx.getSampleNames();
//                        for(String name: sampleNames){
//                            count += vctx.getGenotype(name).countAllele(alt);
//                        }
//                        alleleFreq = count/total;

                        System.out.printf("＊%s has AF of %\nf",rsid,alleleFreq);

                        return new Pair(rsid,alleleFreq);
                    }
                }).coalesce(1).saveAsTextFile("/home/c4lab/yuta/result.txt");//.collect();


//        List<Float> output =
//                vctx.filter(new Function<VariantContext, Boolean>() {
//                    @Override
//                    public Boolean call(VariantContext innerVctx) throws Exception {
//                        // TODO: 判斷rsID在不在
//                        return rsIDs.contains(innerVctx.getID());
//                    }
//                }).map(new Function<VariantContext, Float>() {
//                    @Override
//                    public Float call(VariantContext vctx) throws Exception {
//                        Float alleleFreq = 0.0f;
//                        // TODO:加入你們算AF的邏輯在這裡
//                        float total = vctx.getCalledChrCount();    //5008
//                        float count = 0.0f;
//                        Allele alt = vctx.getAlternateAllele(0);    // [G]
//                        Set<String> sampleNames = vctx.getSampleNames();
//                        for(String name: sampleNames){
//                            count += vctx.getGenotype(name).countAllele(alt);
//                        }
//                        alleleFreq = count/total;
//
//                        return alleleFreq;
//                    }
//                }).collect();

        //TODO:Output結果
//        for(Pair p:outputAsPairs){
//            System.out.printf("%s has AF of %\nf",p.getRSID(),p.getAF());
//        }

        // timer and finishing messange

        long totalms = System.currentTimeMillis()-startTimems;
        int sec = (int) (totalms / 1000) % 60 ;
        int min = (int) ((totalms / (1000*60)) % 60);
        int hr   = (int) ((totalms / (1000*60*60)) % 24);
        System.out.printf("Program Finished. Runtime: %dhr %dmin %dsec (%d ms)\n",hr,min,sec,totalms);

    }

    public static class Pair<String,Float> {

        private final String rsid;
        private final Float af;

        public Pair(String rsid, Float af) {
            this.rsid = rsid;
            this.af = af;
        }

        public String getRSID(){return this.rsid;}
        public Float getAF(){return this.af;}

    }
}
