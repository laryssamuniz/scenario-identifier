package br.uece.lotus.scenario;

import br.uece.lotus.Transition;
import br.uece.lotus.scenario.purpose.TestPurposeSuite;
import br.uece.lotus.scenario.struct.LtsInfo;
import br.uece.lotus.scenario.struct.TestBundle;
import br.uece.lotus.scenario.utils.StateUtils;
import java.util.ArrayList;
import java.util.List;

public class TestBundleBuilder {

    static public TestBundle build(LtsInfo lts, TestPurposeSuite suite) {
        
        List<Transition> initTransitions = new ArrayList<>();
        List<Transition> additionalFinalTransitions = new ArrayList<>();

        if (suite == null){
            initTransitions.addAll(StateUtils.getOutgoingTransitions(lts.getRootState()));
        } else {
            if (suite.getInitialTransitions() == null){
                initTransitions.addAll(StateUtils.getOutgoingTransitions(lts.getRootState()));
            }else{
                initTransitions.addAll(suite.getInitialTransitions());
            }

            if (suite.getAdditionalFinalTransitions() != null) {
                additionalFinalTransitions.addAll(suite.getAdditionalFinalTransitions());
            }
        }

        TestBundle bundle = new TestBundle(initTransitions,additionalFinalTransitions);

        return bundle;
    }
}
