package br.uece.lotus.scenario.gui;

import br.uece.lotus.Component;
import br.uece.lotus.State;
import br.uece.lotus.Transition;
import br.uece.lotus.scenario.TestBundleBuilder;
import br.uece.lotus.scenario.generator.PathGenAllOneLoop;
import br.uece.lotus.scenario.struct.LtsInfo;
import br.uece.lotus.scenario.struct.PathSet;
import br.uece.lotus.viewer.ComponentViewImpl;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javax.swing.JOptionPane;

public class MainWindowController implements Initializable {

    @FXML
    private ScrollPane mScrollPane;

    protected ComponentViewImpl mViewer;

    @FXML
    protected Button mButtonExport;

    @FXML
    private TableView<MainWindowController.PathView> mTableView;

    @FXML
    private TableColumn<MainWindowController.PathView, String> mCollumnName;

    @FXML
    private TableColumn<MainWindowController.PathView, String> mCollumnList;

    @FXML
    private TableColumn<MainWindowController.PathView, String> mCollumnProbability;

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
        
        mButtonExport.setOnAction((ActionEvent event) -> {
            
            if (mTableView.getSelectionModel().getSelectedItems().isEmpty()){
                return;
            }
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Scenarios files");
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Scenarios XML (*.xml)", "*.xml")
            );
            
            File file = fileChooser.showSaveDialog(null);
            
            if (file != null){
                try{
                    exportToXML(file);
                    JOptionPane.showMessageDialog(null, "File Successfully Saved");
                } catch (IOException e){
                    JOptionPane.showMessageDialog(null, "File was not Saved succesfully");
                }
            }
        });
    }
    
    private void initEssential() {
        mLtsInfo = new LtsInfo(mComponent.getInitialState());
    }
    
    private void fillTable(PathSet pathSet) {
        
        mScenarios.clear();
        
        Integer count = 0;
        
        for (List<Transition> transitionList : pathSet.getPathList()) {
            String nameScenario = "Scenario" + count;
            mScenarios.add(new PathView(transitionList, nameScenario));
            count++;
        }
    }

    private void initTable() {
        
        mCollumnName.setCellValueFactory(new PropertyValueFactory<>("pathLabels"));
        mCollumnList.setCellValueFactory(new PropertyValueFactory<>("pathStates"));
        mCollumnProbability.setCellValueFactory(new PropertyValueFactory<>("pathProbability")); 

        mCollumnProbability.setCellFactory(TextFieldTableCell.forTableColumn()); 
        
        mCollumnProbability.setOnEditCommit((CellEditEvent<MainWindowController.PathView, String> t) -> {
            ((PathView) t.getTableView().getItems().get(t.getTablePosition().getRow())).setPathProbability(t.getNewValue());
        });
        
        mTableView.setItems(mScenarios);
        mTableView.setOnMouseClicked(onTableClick);
        
    }

    public class PathView {
        
        PathView(List<Transition> path, String nameScenario) {
            
            mPathLabels = "";
            mPathActions = 0;
            mPathStates = "";
            mPathGuards = "";
            mPathProbability = "";
        
            mTransitionList = path;
            mStatesList = new ArrayList<>();
            
            boolean initialTransition = true;
                        
            for (Transition transition : path) {
                
                if(!mStatesList.contains(transition.getDestiny())){
                    mStatesList.add(transition.getDestiny());
                }
                
                if(!mStatesList.contains(transition.getSource())){
                    mStatesList.add(transition.getSource());
                }
                
                mPathLabels = nameScenario;
                mPathStates += transition.getDestiny().getLabel() + ", ";

                if (initialTransition) {
                    mPathStates = transition.getSource().getLabel() + ", " + mPathStates;
                }

                if (transition.getGuard() != null) {
                    mPathGuards += transition.getGuard() + ", ";
                }

                initialTransition = false;
                
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
        private String mPathStates;
        private String mPathGuards;
        private Integer mPathActions;
        private String mPathProbability;

        public List<State> mStatesList;
        public List<Transition> mTransitionList;

        public String getPathLabels() {
            return mPathLabels;
        }

        public Integer getPathActions() {
            return mPathActions;
        }

        public String getPathProbability() {
            return mPathProbability;
        }

        public String getPathStates() {
            return mPathStates;
        }

        public String getPathGuards() {
            return mPathGuards;
        }

        public void setPathProbability(String mPathProbability) {
            this.mPathProbability = mPathProbability;
        }
    }
    
    protected void exportToXML(File file) throws IOException{
        
        try (PrintStream out = new PrintStream(file)){
            
            String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<project version=\"1.0\" name=\""
                    + mComponent.getName() + "\">\n";
            
            for(PathView path : mScenarios){
                
                content += "\t<component name=\""+ path.getPathLabels() +"\">\n"+
                        "\t\t<states>\n";
                
                Integer initial = 0;
                
                for(State state : path.mStatesList){
                    
                    content += "\t\t\t<state id=\""+state.getID()+"\" x=\""+state.getLayoutX()+"\" y=\""
                            +state.getLayoutY()+"\" label=\""+state.getLabel()+"\"";
                    
                    if (initial++ == 0) {
                        content += " initial=\"true\"";    
                    }
                    
		    content += ">\n\t\t\t</state>\n";
                }
                
                content += "\t\t</states>\n";
                content += "\t\t<transitions>\n";
                
                initial = 0;
                
                for(Transition transition : path.mTransitionList){
                
                    content += "\t\t\t<transition from=\""+transition.getSource().getID() +"\" to=\""
                            +transition.getDestiny().getID() +"\" ";
                            
                        if(initial++ == 0){
                            
                            String str = path.getPathProbability();
                            
                            if (str.isEmpty()) {
                                str = "0";
                            }
                            
                            content += "prob=\""+ str +"\"";
                        }else{
                            content += "prob=\"1\"";
                        }
                                    
                    content += " label=\""+transition.getLabel()+"\" view-type=\"0\">";
                    content += "\t\t\t</transition>\n";
                }
               
                content += "\t\t</transitions>\n";
                content += "</component>\n";
            }
            
            content += "</project>\n";
            
            out.print(content);
        }
    }
    
    protected void showChoices(State state) {
        
        applyEnableStyle(state);
        
        for (Transition transition : state.getOutgoingTransitions()) {
            applyChoiceStyle(transition);
            applyChoiceStyle(transition.getDestiny());
        }
    }

    protected void applyEnableStyle(State state) {
        state.setColor(null);
        state.setTextColor("black");
        state.setTextSyle(State.TEXTSTYLE_NORMAL);
        state.setBorderColor("black");
        state.setBorderWidth(1);
    }

    protected void applyEnableStyle(Transition transition) {
        transition.setColor("black");
        transition.setTextSyle(Transition.TEXTSTYLE_NORMAL);
        transition.setTextColor("black");
        transition.setWidth(1);
    }

    protected void applyDisableAll() {
        
        State state = mComponent.getInitialState();
        
        ArrayList<State> stateList = new ArrayList<>();
        ArrayList<Transition> visitedTransitions = new ArrayList<>();
        
        int count = 0;
        
        stateList.add(state);

        while (count < stateList.size()) {
            
            state = stateList.get(count);
            applyDisabledStyle(state);
            
            for (Transition transition : state.getOutgoingTransitions()) {
                
                if (!stateList.contains(transition.getDestiny())) {
                    stateList.add(transition.getDestiny());
                }
                
                if (!visitedTransitions.contains(transition)) {
                    applyDisabledStyle(transition);
                    visitedTransitions.add(transition);
                }
            }
            
            ++count;
        }
    }

    protected void applyDisabledStyle(State state) {
        state.setColor("#d0d0d0");
        state.setTextColor("#c0c0c0");
        state.setTextSyle(State.TEXTSTYLE_NORMAL);
        state.setBorderColor("gray");
        state.setBorderWidth(1);
    }

    protected void applyDisabledStyle(Transition transition) {
        transition.setColor("#d0d0d0");
        transition.setTextColor("#c0c0c0");
        transition.setTextSyle(Transition.TEXTSTYLE_NORMAL);
        transition.setWidth(1);
    }

    private void applyChoiceStyle(Transition transition) {
        transition.setColor("blue");
        transition.setTextSyle(Transition.TEXTSTYLE_BOLD);
        transition.setTextColor("blue");
        transition.setWidth(2);
    }

    private void applyChoiceStyle(State state) {
        state.setColor(null);
        state.setBorderColor("blue");
        state.setTextSyle(Transition.TEXTSTYLE_BOLD);
        state.setTextColor("blue");
        state.setBorderWidth(2);
    }

    protected EventHandler<? super MouseEvent> onTableClick = (MouseEvent e) -> {
        
        PathView selectedPath = mTableView.getSelectionModel().getSelectedItem();
        
        if (selectedPath == null) {
            System.out.println("PATH VIEW NULL");
            return;
        }
        
        applyDisableAll();
        
        for (Transition transition : selectedPath.mTransitionList) {
            applyEnableStyle(transition);
            applyEnableStyle(transition.getDestiny());
            applyEnableStyle(transition.getSource());
        }
    };
}