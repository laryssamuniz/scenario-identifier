package br.uece.lotus.scenario.purpose;

import br.uece.lotus.Transition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestPurposeSuite{

    private Set<Transition> mInitialTransitions;
    private Set<Transition> mAdditionalFinalTransitions;

    private List<String> mBeginTransitionLabels;
    private List<List<String>> mMiddleTransitionLabels;
    private List<String> mFinalTransitionLabels;
    private boolean mAccept;

    public TestPurposeSuite(){
        mInitialTransitions = new HashSet<>();
        mAdditionalFinalTransitions = new HashSet<>();

        mBeginTransitionLabels = new ArrayList<>();
        mMiddleTransitionLabels = new ArrayList<>();
        mFinalTransitionLabels = new ArrayList<>();
    }

    public void setInitialTransitions(Set<Transition> initialTransitions){
        mInitialTransitions = initialTransitions;
    }

    public void setAdditionalFinalTransitions(Set<Transition> finalTransitions){
        mAdditionalFinalTransitions = finalTransitions;
    }

    public Set<Transition> getInitialTransitions(){
        return mInitialTransitions;
    }

    public Set<Transition> getAdditionalFinalTransitions(){
        return mAdditionalFinalTransitions;
    }

    public String getFirstTransitionLabel(){
        
        if (!mBeginTransitionLabels.isEmpty()){
            return mBeginTransitionLabels.get(0);
        }
        return null;
    }

    public String getLastTransitionLabel(){
        
        if (!mFinalTransitionLabels.isEmpty()){
            return mFinalTransitionLabels.get(mFinalTransitionLabels.size() - 1);
        }
        return null;
    }

    public boolean acceptPaths(){
        return mAccept;
    }

    public void setAcceptPaths(boolean b){
        mAccept = b;
    }

    public void addBeginTransitionsLabel(List<String> label){
        mBeginTransitionLabels.addAll(label);
    }

    public void addMiddleTransitionLabel(List<String> label){
        mMiddleTransitionLabels.add(label);
    }

    public void addFinalTransitionLabel(List<String> label){
        mFinalTransitionLabels.addAll(label);
    }

    public List<String> getBeginLabels(){
        return mBeginTransitionLabels;
    }

    public List<List<String>> getMiddleLabels(){
        return mMiddleTransitionLabels;
    }

    public List<String> getFinalLabels(){
        return mFinalTransitionLabels;
    }

    public String getPathRepresentation(List<String> list){
        
        String ret = "@";

        for (String str : list){
            ret = ret + str + "@";
        }

        return ret;
    }

}
