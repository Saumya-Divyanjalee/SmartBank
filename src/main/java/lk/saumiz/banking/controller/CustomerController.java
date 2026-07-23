package lk.saumiz.banking.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.saumiz.banking.bo.CustomerBO;
import lk.saumiz.banking.bo.custom.BOFactory;
import lk.saumiz.banking.dto.CustomerDTO;
import lk.saumiz.banking.entity.Customer;

public class CustomerController {

    @FXML private TextField txtCustomerId;
    @FXML private TextField txtName;
    @FXML private TextField txtNic;
    @FXML private TextField txtPhone;
    @FXML private TextField txtAddress;
    @FXML private TextField txtEmail;
    @FXML private TextField txtSearch;
    @FXML private Label lblMessage;

    @FXML private TableView<Customer> tblCustomers;
    @FXML private TableColumn<Customer, String> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colNic;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colAddress;
    @FXML private TableColumn<Customer, String> colEmail;

    private final CustomerBO customerBO = BOFactory.getInstance().getCustomerBO();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colNic.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) populateForm(newV);
        });

        handleRefresh();
    }

    @FXML
    private void handleAdd() {
        try {
            CustomerDTO dto = readForm(false);
            String newId = customerBO.createCustomer(dto);
            if (newId != null) {
                lblMessage.setText("Customer created: " + newId);
                handleClear();
                handleRefresh();
            } else {
                lblMessage.setText("Failed to create customer.");
            }
        } catch (Exception e) {
            lblMessage.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            if (txtCustomerId.getText() == null || txtCustomerId.getText().isBlank()) {
                lblMessage.setText("Select a customer from the table first.");
                return;
            }
            CustomerDTO dto = readForm(true);
            boolean ok = customerBO.updateCustomer(dto);
            lblMessage.setText(ok ? "Customer updated." : "Update failed.");
            handleRefresh();
        } catch (Exception e) {
            lblMessage.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        try {
            String id = txtCustomerId.getText();
            if (id == null || id.isBlank()) {
                lblMessage.setText("Select a customer from the table first.");
                return;
            }
            boolean ok = customerBO.deleteCustomer(id);
            lblMessage.setText(ok ? "Customer deleted." : "Delete failed.");
            handleClear();
            handleRefresh();
        } catch (Exception e) {
            lblMessage.setText("Error: " + e.getMessage() +
                    " (tip: a customer with existing accounts may be blocked by a foreign key)");
        }
    }

    @FXML
    private void handleClear() {
        txtCustomerId.clear();
        txtName.clear();
        txtNic.clear();
        txtPhone.clear();
        txtAddress.clear();
        txtEmail.clear();
        lblMessage.setText("");
        tblCustomers.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSearch() {
        try {
            String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
            ObservableList<Customer> results = FXCollections.observableArrayList(customerBO.searchCustomers(keyword));
            tblCustomers.setItems(results);
        } catch (Exception e) {
            lblMessage.setText("Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            ObservableList<Customer> all = FXCollections.observableArrayList(customerBO.getAllCustomers());
            tblCustomers.setItems(all);
        } catch (Exception e) {
            lblMessage.setText("Could not load customers: " + e.getMessage());
        }
    }

    private void populateForm(Customer c) {
        txtCustomerId.setText(c.getCustomerId());
        txtName.setText(c.getName());
        txtNic.setText(c.getNic());
        txtPhone.setText(c.getPhone());
        txtAddress.setText(c.getAddress());
        txtEmail.setText(c.getEmail());
    }

    private CustomerDTO readForm(boolean includeId) {
        CustomerDTO dto = new CustomerDTO();
        if (includeId) dto.setCustomerId(txtCustomerId.getText());
        dto.setName(txtName.getText());
        dto.setNic(txtNic.getText());
        dto.setPhone(txtPhone.getText());
        dto.setAddress(txtAddress.getText());
        dto.setEmail(txtEmail.getText());
        return dto;
    }
}
