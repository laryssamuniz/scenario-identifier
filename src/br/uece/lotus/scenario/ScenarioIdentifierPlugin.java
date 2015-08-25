package br.uece.lotus.scenario;

import br.uece.lotus.Component;
import br.uece.lotus.project.ProjectExplorer;
import br.uece.seed.app.UserInterface;
import br.uece.seed.ext.ExtensionManager;
import br.uece.seed.ext.Plugin;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javax.swing.JOptionPane;

public class ScenarioIdentifierPlugin extends Plugin implements ScenarioIdentifier {

    private UserInterface mUserInterface;
    private ProjectExplorer mProjectExplorer;

    private Mode windowMode;

    public static enum Mode {

        Statistical, Functional, Exception
    };

    @Override
    public void show(Component c, boolean editable) {

        URL location;

        location = getClass().getResource("gui/MainWindow.fxml");

        FXMLLoader loader = new FXMLLoader();

        ResourceBundle bundle = new ResourceBundle() {
            Component mComponent = c;

            @Override
            protected Object handleGetObject(String key) {                
                if (key == "component") {
                    return mComponent;
                }
                return null;
            }

            @Override
            public Enumeration<String> getKeys() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        try {
            loader.setClassLoader(getClass().getClassLoader());
            loader.setLocation(location);
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setResources(bundle);
            Parent root = (Parent) loader.load(location.openStream());

            int id = mUserInterface.getCenterPanel().newTab(c.getName() + " - [ScenarioIdentifier]", root, true);
            mUserInterface.getCenterPanel().showTab(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable mSelectScenario = () -> {

        Component c = mProjectExplorer.getSelectedComponent();

        try {
            if (c == null) {
                JOptionPane.showMessageDialog(null, "Select a component!");
                return;
            }

            windowMode = Mode.Functional;
            
            show(c.clone(), true);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ScenarioIdentifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    };

    private Runnable mAboutRunnable = () -> {};

    @Override

    public void onStart(ExtensionManager extensionManager) throws Exception {

        mUserInterface = extensionManager.get(UserInterface.class);
        mProjectExplorer = extensionManager.get(ProjectExplorer.class);

        mUserInterface.getMainMenu().newItem("Scenario Identifier/Select Scenarios").setWeight(1).setAction(mSelectScenario).create();
//        mUserInterface.getMainMenu().newItem("Scenario Identifier/About").setWeight(1).setAction(mAboutRunnable).create();

    }

}
