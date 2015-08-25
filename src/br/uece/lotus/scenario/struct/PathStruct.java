package br.uece.lotus.scenario.struct;

import br.uece.lotus.State;
import br.uece.lotus.Transition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * This class was designed to generate and help the generation algorithms with
 * the construction of a new path.
 *
 * It construct and maintain informations about the path, like the cycles
 * passed, last cycle passed, repeated states and the own path.
 */
public class PathStruct{

    private List<Transition> mPath;
    private Set<Transition> mVisitedTransitions;
    private Set<State> mVisitedStates;
    private Set<State> mRepeatedStates;
    private Set<List<Transition>> mCyclesListTransitions;
    private Set<List<State>> mCycleListStates;
    private Map<List<State>,Set<List<Transition>>> mCycleStateToTransitionsMap;
    private List<State> mStateLastCycle;
    private List<Transition> mTransitionLastCycle;

    public Transition getInitialTransition(){
        return mPath.get(0);
    }

    public State getInitialState(){
        return getInitialTransition().getSource();
    }

    public State getCurrentState(){
        return getLastTransition().getDestiny();
    }

    public Transition getLastTransition(){
        return mPath.get(mPath.size() - 1);
    }

    public boolean containsTransition(Transition t){
        return mVisitedTransitions.contains(t);
    }

    public boolean containsState(State s){
        return mVisitedStates.contains(s);
    }

    public boolean containsStateCycle(List<State> cycle){
        return mCycleListStates.contains(cycle);
    }

    public boolean containsTransitionCycle(List<Transition> cycle){
        return mCyclesListTransitions.contains(cycle);
    }

    public int getStatesCyclesCount(){
        return mCycleListStates.size();
    }

    public int getTransitionsCyclesCount(){
        return mCyclesListTransitions.size();
    }

    public int getRepeatedStatesCount(){
        return mRepeatedStates.size();
    }

    public List<State> getStateLastCycle(){
        return mStateLastCycle;
    }

    public List<Transition> getTransitionLastCycle(){
        return mTransitionLastCycle;
    }

    public List<Transition> getPath(){
        return mPath;
    }

    public Set<Transition> getVisitedTransitions(){
        return mVisitedTransitions;
    }

    public Set<State> getVisitedStates(){
        return mVisitedStates;
    }

    public Set<State> getRepeatedStates(){
        return mRepeatedStates;
    }

    public Set<List<State>> getStatesCycleList(){
        return mCycleListStates;
    }

    public Set<List<Transition>> getTransitionsCycleList(){
        return mCyclesListTransitions;
    }

    public Set<List<Transition>> getTransitionsCyclesByCycleState(List<State> cycle){
        return mCycleStateToTransitionsMap.get(cycle);
    }

    protected Map<List<State>, Set<List<Transition>>> getCycleStateToTransitionMap(){
        return mCycleStateToTransitionsMap;
    }

    public PathStruct(){
        init();
    }

    public PathStruct(List<Transition> list){
        
        init();
        for (Transition t : list){
            addTransition(t);
        }
    }

    public PathStruct(PathStruct p){
        init(p);
    }

    /**
     *
     * @param t -> transition to be added
     * @return -> true, if the transition added has formed a cycle or false,
     * case not
     */
    public boolean addTransition(Transition t){
        
        if (mPath.isEmpty()){
            addState(t.getSource());
        }

        mPath.add(t);
        mVisitedTransitions.add(t);

        boolean cycleFormed = addState(t.getDestiny());

        if (cycleFormed){
            
            // Stores the last cycle
            int begin = mPath.size() - mStateLastCycle.size();

            List<Transition> cycle = new ArrayList<>(mPath.subList(begin, mPath.size() - 1));

            mTransitionLastCycle = new ArrayList<>(cycle);

            if (!mCyclesListTransitions.contains(cycle)){
                mCyclesListTransitions.add(cycle);

                Set<List<Transition>> transitionSet;

                if (mCycleStateToTransitionsMap.containsKey(mStateLastCycle)){
                    transitionSet = mCycleStateToTransitionsMap.get(mStateLastCycle);
                } else{
                    transitionSet = new HashSet<>();
                }

                transitionSet.add(cycle);

                mCycleStateToTransitionsMap.put(mStateLastCycle, transitionSet);
            }
        }

        return cycleFormed;
    }

    /**
     *
     * @param s -> state to be added
     * @return -> true, if the state added formed a cyrcle or false, case not
     */
    private boolean addState(State s){
        
        boolean ret = false;

        if (mVisitedStates.contains(s)){ // Cycle found
            
            mRepeatedStates.add(s);

            List<State> cycle = new ArrayList<>();

            for (int i = mPath.size() - 1; i >= 0; --i){
                
                Transition t = mPath.get(i);
                cycle.add(0, s);

                if (t.getSource() == s){
                    break;
                }
                
            }

            mStateLastCycle = new ArrayList<>(cycle);

            if (!mCycleListStates.contains(cycle)){
                mCycleListStates.add(cycle);
            }

            ret = true;
        }

        mVisitedStates.add(s);

        return ret;
    }

    private void init(){
        
        mPath = new ArrayList<>();
        mTransitionLastCycle = new ArrayList<>();
        mStateLastCycle = new ArrayList<>();

        mVisitedTransitions = new HashSet<>();
        mVisitedStates = new HashSet<>();
        mRepeatedStates = new HashSet<>();
        mCycleListStates = new HashSet<>();
        mCyclesListTransitions = new HashSet<>();

        mCycleStateToTransitionsMap = new HashMap<>();
        
    }

    private void init(PathStruct ps){
        
        init();

        mPath.addAll(ps.getPath());
        mTransitionLastCycle.addAll(ps.getTransitionLastCycle());
        mStateLastCycle.addAll(ps.getStateLastCycle());

        mVisitedTransitions.addAll(ps.getVisitedTransitions());
        mVisitedStates.addAll(ps.getVisitedStates());
        mRepeatedStates.addAll(ps.getRepeatedStates());
        mCycleListStates.addAll(ps.getStatesCycleList());
        mCyclesListTransitions.addAll(ps.getTransitionsCycleList());

        mCycleStateToTransitionsMap.putAll(ps.getCycleStateToTransitionMap());
        
    }
}
