package br.uece.lotus.scenario.struct;

import br.uece.lotus.Transition;
import java.util.ArrayList;
import java.util.List;

public class TestBundle{

    private List<Transition> mInitialTransitionList;
    private List<Transition> mAdditionalFinalTransitionList;

    public List<Transition> getInitialTransitionList(){
        return mInitialTransitionList;
    }

    public List<Transition> getAdditionalFinalTransitionList(){
        return mAdditionalFinalTransitionList;
    }

    public TestBundle(List<Transition> initialTransitionList, List<Transition> additionalFinalTransitions){
        
        if (initialTransitionList != null){
            mInitialTransitionList = initialTransitionList;
        }else{
            mInitialTransitionList = new ArrayList<>();
        }

        if (additionalFinalTransitions != null){
            mAdditionalFinalTransitionList = additionalFinalTransitions;
        }else{
            mAdditionalFinalTransitionList = new ArrayList<>();
        }        
    }
}
