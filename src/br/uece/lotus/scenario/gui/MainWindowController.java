package br.uece.lotus.scenario.gui;

import br.uece.lotus.Component;
import br.uece.lotus.State;
import br.uece.lotus.Transition;
import br.uece.lotus.scenario.TestBundleBuilder;
import br.uece.lotus.scenario.generator.PathGenAllOneLoop;
import br.uece.lotus.scenario.struct.LtsInfo;
import br.uece.lotus.scenario.struct.PathSet;
import br.uece.lotus.viewer.ComponentViewImpl;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class MainWindowController implements Initializable {

    @FXML
    private ScrollPane mScrollPane;

    protected ComponentViewImpl mViewer;

    @FXML
    private TableView<MainWindowController.PathView> mTableView;

    @FXML
    private TableColumn<MainWindowController.PathView, String> mCollumnName;

    @FXML
    private TableColumn<MainWindowController.PathView, String> mCollumnList;

    @FXML
    private TableColumn<MainWindowController.PathView, Double> mCollumnProbability;

    protected ObservableList<MainWindowController.PathView> mScenarios = FXCollections.observableArrayList();
    
    LtsInfo mLtsInfo;
    PathGenAllOneLoop mPathGenerator = new PathGenAllOneLoop();
    Component mComponent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mViewer = new ComponentViewImpl();
        mScrollPane.setContent(mViewer);

        mComponent = (Component) resources.getObject("component");
        mViewer.setComponent(mComponent);
        
        mTableView.setEditable(true);
        
        initTable();
        initEssential();
        
        PathSet pathSet = mPathGenerator.generate(mLtsInfo, TestBundleBuilder.build(mLtsInfo, null));     
        fillTable(pathSet);
    }
    
    private void initEssential() {
        mLtsInfo = new LtsInfo(mComponent.getInitialState());
    }
    
    private void fillTable(PathSet pathSet) {
        
        mScenarios.clear();
        
        for (List<Transition > transitionList : pathSet.getPathList()) {
            mScenarios.add(new PathView(transitionList));
        }
    }

    private void initTable() {
        
        mCollumnName.setCellValueFactory(new PropertyValueFactory<>("pathLabels"));
        mCollumnList.setCellValueFactory(new PropertyValueFactory<>("pathStates"));
        mCollumnProbability.setCellValueFactory(new PropertyValueFactory<>("pathProbability"));
        mCollumnProbability.setOnEditCommit(
        new EventHandler<CellEditEvent<MainWindowController.PathView, Double>>() {
            @Override
            public void handle(CellEditEvent<MainWindowController.PathView, Double> t) {
                ((PathView) t.getTableView().getItems().get(
                    t.getTablePosition().getRow())
                    ).setPathProbability(t.getNewValue());
            }
        }
    );
        
        mTableView.setItems(mScenarios);
        mTableView.setOnMouseClicked(onTableClick);
        
    }

    public class PathView {
        
        PathView(List<Transition> path) {
            
            mPathLabels = "";
            mPathActions = 0;
            mPathStates = "";
            mPathGuards = "";
            mPathProbability = 1.0;
            boolean initialTransition = true;
            mTransitionList = path;
            
            int count = 0;
            
            for (Transition t : path) {
                
                mPathLabels = "Scenario " + count ;
                mPathStates += t.getDestiny().getLabel() + ", ";

                if (initialTransition) {
                    mPathStates = t.getSource().getLabel() + ", " + mPathStates;
                }

                if (t.getGuard() != null) {
                    mPathGuards += t.getGuard() + ", ";
                }

                initialTransition = false;

                double p = 0.0;

                if (t.getProbability() != null){
                    p = t.getProbability();
                }

                mPathProbability *= p;
                
                count++;
            }

            if (path.size() > 0) {
                
                mPathStates = mPathStates.substring(0, mPathStates.length() - 2);
                
                if (!mPathGuards.isEmpty()) {
                    mPathGuards = mPathGuards.substring(0, mPathGuards.length() - 2);
                }

                
                mPathActions = path.size();
            }
        }

        private String mPathLabels;
        private Integer mPathActions;
        private String mPathStates;
        private String mPathGuards;
        private Double mPathProbability;

        public List<Transition> mTransitionList;

        public String getPathLabels() {
            return mPathLabels;
        }

        public Integer getPathActions() {
            return mPathActions;
        }

        public Double getPathProbability() {
            return mPathProbability;
        }

        public String getPathStates() {
            return mPathStates;
        }

        public String getPathGuards() {
            return mPathGuards;
        }

        public void setPathProbability(Double mPathProbability) {
            this.mPathProbability = mPathProbability;
        }
        
        
        
    }
    
    protected void showChoices(State state) {
        
        applyEnableStyle(state);
        
        for (Transition t : state.getOutgoingTransitions()) {
            applyChoiceStyle(t);
            applyChoiceStyle(t.getDestiny());
        }
    }

    protected void applyEnableStyle(State s) {
        s.setColor(null);
        s.setTextColor("black");
        s.setTextSyle(State.TEXTSTYLE_NORMAL);
        s.setBorderColor("black");
        s.setBorderWidth(1);
    }

    protected void applyEnableStyle(Transition t) {
        t.setColor("black");
        t.setTextSyle(Transition.TEXTSTYLE_NORMAL);
        t.setTextColor("black");
        t.setWidth(1);
    }

    protected void applyDisableAll() {
        
        State s = mComponent.getInitialState();
        ArrayList<State> stateList = new ArrayList<>();
        ArrayList<Transition> visitedTransitions = new ArrayList<>();
        int i = 0;
        stateList.add(s);

        while (i < stateList.size()) {
            s = stateList.get(i);
            applyDisabledStyle(s);
            for (Transition t : s.getOutgoingTransitions()) {
                if (!stateList.contains(t.getDestiny())) {
                    stateList.add(t.getDestiny());
                }
                if (!visitedTransitions.contains(t)) {
                    applyDisabledStyle(t);
                    visitedTransitions.add(t);
                }
            }
            ++i;
        }
    }

    protected void applyDisabledStyle(State s) {
        s.setColor("#d0d0d0");
        s.setTextColor("#c0c0c0");
        s.setTextSyle(State.TEXTSTYLE_NORMAL);
        s.setBorderColor("gray");
        s.setBorderWidth(1);
    }

    protected void applyDisabledStyle(Transition t) {
        t.setColor("#d0d0d0");
        t.setTextColor("#c0c0c0");
        t.setTextSyle(Transition.TEXTSTYLE_NORMAL);
        t.setWidth(1);
    }

    private void applyChoiceStyle(Transition t) {
        t.setColor("blue");
        t.setTextSyle(Transition.TEXTSTYLE_BOLD);
        t.setTextColor("blue");
        t.setWidth(2);
    }

    private void applyChoiceStyle(State s) {
        s.setColor(null);
        s.setBorderColor("blue");
        s.setTextSyle(Transition.TEXTSTYLE_BOLD);
        s.setTextColor("blue");
        s.setBorderWidth(2);
    }

    protected EventHandler<? super MouseEvent> onTableClick = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {
            PathView selectedPath = mTableView.getSelectionModel().getSelectedItem();

            if (selectedPath == null) {
                System.out.println("PATH VIEW NULL");
                return;
            }

            applyDisableAll();

            for (Transition t : selectedPath.mTransitionList) {
                applyEnableStyle(t);
                applyEnableStyle(t.getDestiny());
                applyEnableStyle(t.getSource());
            }
        }
    };
}