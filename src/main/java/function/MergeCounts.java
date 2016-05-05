package function;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yat on 2016/5/5.
 */
public class MergeCounts implements Function2<List<Integer>, List<Integer>, List<Integer>> {
    @Override
    public List<Integer> call(List<Integer> integers1, List<Integer> integers2) throws Exception {
        List<Integer> output = new ArrayList<>();
        int i = 0;
        for(Integer int1: integers1){
            output.set(i, int1+ integers2.get(i));
            i++;
        }
        return output;
    }
}
