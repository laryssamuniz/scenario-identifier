package br.uece.lotus.scenario.struct;

import br.uece.lotus.State;
import br.uece.lotus.Transition;
import br.uece.lotus.scenario.utils.StateUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javafx.util.Pair;

/**
 *
 * This class holds info related to the current LTS selected
 * by the user and passed to TCG instance
 */
public class LtsInfo {

    private Set<State> mStateList;
    private Set<State> mSelfLoopStateSet;
    private Set<State> mFinalStateList;
    private Set<State> mErrorStateList;
    private Set<Transition> mTransitionSet;
    private Map<String,Set<Transition>> mLabelToTransitionsMap;
    private Map<Integer,State> mStateMap;
    private Map<Integer,Set<State>> mAdjacencyTable;
    private Map<Integer,Set<State>> mStateRangeTable;
    private Map<Integer,Set<Transition>> mStateTransitionRangeTable;
    private Map<Pair<Integer,Integer>, List<Transition>> mStateToStateTransitionMap;
    private State mRoot;

    public LtsInfo(State root){        
        mRoot = root;
        init();
        fillInfo(root);
    }

    public Set<State> getSelfLoopStateSet(){
        return mSelfLoopStateSet;
    }

    public State getRootState(){
        return mRoot;
    }

    public Set<State> getAdjacencyStates(State state){
        return mAdjacencyTable.get(state.getID());
    }

    public Set<State> getStateRange(State state){
        return mStateRangeTable.get(state.getID());
    }

    public Set<Transition> getTransitionRange(State state) {
        return mStateTransitionRangeTable.get(state.getID());
    }

    public List<Transition> getStateToStateTransitions(State source, State destiny){
        Pair<Integer,Integer> pair = new Pair<>(source.getID(), destiny.getID());
        return mStateToStateTransitionMap.get(pair);
    }

    public Set<Transition> getTransitionsByLabel(String label){
        
        if (label != null){
            return mLabelToTransitionsMap.get(label.toLowerCase());
        }
        return null;
    }

    public int getTransitionsCount(){
        return mTransitionSet.size();
    }

    public int getStatesCount(){
        return mStateList.size();
    }

    private void init(){
        
        mAdjacencyTable = new HashMap<>();
        mStateRangeTable = new HashMap<>();
        mStateTransitionRangeTable = new HashMap<>();
        mStateToStateTransitionMap = new HashMap<>();
        mLabelToTransitionsMap = new HashMap<>();
        mStateMap = new HashMap<>();

        mFinalStateList = new HashSet<>();
        mErrorStateList = new HashSet<>();
        mStateList = new HashSet<>();
        mSelfLoopStateSet = new HashSet<>();
        mTransitionSet = new HashSet<>();
        
    }

    private Pair<Set<State>, Set< Transition>> computeRange(State root){
        
        Stack<State> stack = new Stack<>();
        Set<State> stateList = new HashSet<>();
        Set<Transition> transitionList = new HashSet<>();
        
        boolean addFirstState = false;

        stack.add(root);

        while (!stack.isEmpty()){
            
            State topState = stack.pop();
            stateList.add(topState);

            for (Transition transition : topState.getOutgoingTransitions()){
                
                transitionList.add(transition);

                if (transition.getDestiny() == root){
                    addFirstState = true;
                }

                if (!stateList.contains(transition.getDestiny())){
                    stack.push(transition.getDestiny());
                }
            }
        }

        if (!addFirstState){
            stateList.remove(root);
        }
        
        return new Pair<>(stateList, transitionList);
    }

    private void fillInfo(State root){
        
        Stack<State> stack = new Stack<>();
        stack.push(root);

        boolean removeRoot = true;

        while (!stack.isEmpty()){
            
            State state = stack.pop();
            addState(state);

            if (state != root){
                
                Pair<Set<State>, Set< Transition>> pair = computeRange(state);
                mStateRangeTable.put(state.getID(), pair.getKey());
                mStateTransitionRangeTable.put(state.getID(), pair.getValue());
                
            }

            Set<State> adjacencyList = new HashSet<>();

            for (Transition transition : state.getOutgoingTransitions()){
                
                addTransition(transition);
                adjacencyList.add(transition.getDestiny());

                if (transition.getDestiny() == root){
                    removeRoot = false;
                }

                if (!mStateList.contains(transition.getDestiny())){
                    stack.push(transition.getDestiny());
                }
            }

            mAdjacencyTable.put(state.getID(), adjacencyList);
        }

        Set<State> rootRangeList = new HashSet<>();
        rootRangeList.addAll(mStateList);

        if (removeRoot){
            rootRangeList.remove(root);
        }

        mStateRangeTable.put(root.getID(), rootRangeList);
        mStateTransitionRangeTable.put(root.getID(), mTransitionSet);
    }

    private void addState(State s){
        
        mStateList.add(s);
        mStateMap.put(s.getID(), s);

        if (StateUtils.isSelfLoopState(s)){
            mSelfLoopStateSet.add(s);
        }

        if (s.isFinal()){
            mFinalStateList.add(s);
        }else if (s.isError()){
            mErrorStateList.add(s);
        }
    }

    private void addTransition(Transition t){
        
        String label = "";
        Set<Transition> set;
        List<Transition> listStateToState;

        Pair<Integer,Integer> pair = new Pair<>(t.getSource().getID(), t.getDestiny().getID());

        label = t.getLabel().toLowerCase();
        mTransitionSet.add(t);

        // Add to state to state map
        if (mStateToStateTransitionMap.containsKey(pair)){
            listStateToState = mStateToStateTransitionMap.get(pair);
        }else{
            listStateToState = new ArrayList<>();
        }

        // Add to label transition map
        if (mLabelToTransitionsMap.containsKey(label)){
            set = mLabelToTransitionsMap.get(label);
        }else{
            set = new HashSet<>();
        }

        set.add(t);
        listStateToState.add(t);

        mLabelToTransitionsMap.put(label, set);
        mStateToStateTransitionMap.put(pair, listStateToState);
    }
}
