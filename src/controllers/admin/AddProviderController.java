package controllers.admin;

import com.jfoenix.controls.*;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import database.repos.AdminRepo;
import database.repos.ProvidersRepo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import models.Admin;
import models.Provider;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

public class AddProviderController implements Initializable {

    //MODEL
    private final ProvidersRepo providersRepo = new ProvidersRepo();
    private final AdminRepo adminRepo = new AdminRepo();

    @FXML
    private Label titleLabel;

    @FXML
    private Label idLabel;

    @FXML
    private Label dateJoinedLabel;

    @FXML
    private JFXTextField nameTextField;

    @FXML
    private JFXTextField phoneTextField;

    @FXML
    private JFXTextField addressTextField;

    @FXML
    private JFXComboBox<Admin> adminNameComboBox;

    @FXML
    private void submitClick(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException, IOException {
        if (validate()) {
            if (idLabel.getText().isEmpty()) {
                providersRepo.addNewProvider(UUID.randomUUID().toString().substring(0, 4),
                        nameTextField.getText(),
                        phoneTextField.getText(),
                        addressTextField.getText(),
                        Date.from(Instant.now()),
                        adminNameComboBox.getSelectionModel().getSelectedItem().getId());
            } else {
                providersRepo.updateProvider(
                        new Provider(
                                idLabel.getText(),
                                nameTextField.getText(),
                                phoneTextField.getText(),
                                addressTextField.getText(),
                                Date.from(Instant.now()),
                                adminNameComboBox.getSelectionModel().getSelectedItem().getId()));
            }

            onNavigateBack((Node) mouseEvent.getSource());
        }
    }

    @FXML
    private void backClick(MouseEvent mouseEvent) throws IOException {
        onNavigateBack((Node) mouseEvent.getSource());
    }

    public AddProviderController() throws Exception {

    }

    public void setProviderId(String id) {
        if (id == null) {
            titleLabel.setText("Add New Provider");
        } else {
            setFields(providersRepo.getProviderById(id));
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Callback<ListView<Admin>, ListCell<Admin>> factory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Admin admin, boolean empty) {
                super.updateItem(admin, empty);
                setText(empty ? "" : admin.getName());
            }
        };

        for (Admin admin : adminRepo.getAdmins()) {
            adminNameComboBox.getItems().add(admin);
        }
        adminNameComboBox.setCellFactory(factory);
        adminNameComboBox.setButtonCell(factory.call(new JFXListView<>()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        dateJoinedLabel.setText(formatter.format(Date.from(Instant.now())));

        setValidation();
    }

    private void onNavigateBack(Node node) throws IOException {
        Scene currentScene = node.getScene();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("./../../views/admin/admin-home-screen.fxml")));
        currentScene.setRoot(root);
    }

    private boolean validate() {
        return nameTextField.validate() && phoneTextField.validate() && adminNameComboBox.validate() && addressTextField.validate();
    }

    private void setValidation() {
        nameTextField.setValidators(new RequiredFieldValidator("Name cannot be empty"));
        phoneTextField.setValidators(new RequiredFieldValidator("Phone number cannot be empty"));
        phoneTextField.setValidators(new NumberValidator("Phone number is invalid"));
        addressTextField.setValidators(new RequiredFieldValidator("Address cannot be empty"));
        adminNameComboBox.setValidators(new RequiredFieldValidator("You must specify the admin promoted"));
    }

    private int getAdminIndexInComboBox(String id) {
        for (int i = 0; i < adminNameComboBox.getItems().size(); i++) {
            if (adminNameComboBox.getItems().get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /*
     * Set fields for update by passing values to fields
     * */
    private void setFields(Provider provider) {
        titleLabel.setText("Update " + provider.getName());
        idLabel.setText(provider.getId());
        dateJoinedLabel.setText(provider.getDateJoined().toString());
        nameTextField.setText(provider.getName());
        phoneTextField.setText(provider.getPhone());
        addressTextField.setText(provider.getAddress());
        adminNameComboBox.getSelectionModel().select(getAdminIndexInComboBox(provider.getAdminPromoted()));
    }

}