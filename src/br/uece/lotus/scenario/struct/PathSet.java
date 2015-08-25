package br.uece.lotus.scenario.struct;

import br.uece.lotus.State;
import br.uece.lotus.Transition;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathSet{

    protected Collection<List<Transition>> mPathList;
    protected Set<Transition> mVisitedTransitions;
    protected Set<State> mVisitedStates;
    protected Set<List<Transition>> mCyclesListTransitions;
    protected Set<List<State>> mCycleListStates;

    public PathSet(){
        mPathList = new HashSet<>();
        mVisitedStates = new HashSet<>();
        mVisitedTransitions = new HashSet<>();
        mCyclesListTransitions = new HashSet<>();
        mCycleListStates = new HashSet<>();
    }

    public int getPathsCount(){
        return mPathList.size();
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

    public Collection<List<Transition>> getPathList(){
        return mPathList;
    }

    public Set<State> getVisitedStates(){
        return mVisitedStates;
    }

    public Set<Transition> getVisitedTransitions(){
        return mVisitedTransitions;
    }

    public int getVisitedTransitionsCount(){
        return mVisitedTransitions.size();
    }

    public int getVisitedStatesCount(){
        return mVisitedStates.size();
    }

    public void addPath(PathStruct ps){
        
        mPathList.add(ps.getPath());

        mVisitedTransitions.addAll(ps.getVisitedTransitions());
        mVisitedStates.addAll(ps.getVisitedStates());

        mCycleListStates.addAll(ps.getStatesCycleList());
        mCyclesListTransitions.addAll(ps.getTransitionsCycleList());
        
    }
}
