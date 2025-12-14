package org.toop.app.widget.complex;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class TableWidget<DATATYPE> extends PopupWidget {
    private ObservableList<DATATYPE> serverList = FXCollections.observableArrayList();
    private TableView<DATATYPE> table = new TableView<>();


    public TableWidget(String... columns) {
        var cols = new ArrayList<TableColumn<DATATYPE, String>>();

        for (String column : columns) {
            TableColumn<DATATYPE, String> col = new TableColumn<>(column.toUpperCase());
            col.setCellValueFactory(new PropertyValueFactory<>(column));
            cols.add(col);
        }

        table.getColumns().addAll(cols);
        update();
        onColumnClicked();

        add(Pos.CENTER, table);
    }

    public void add(DATATYPE serverFound) {
        serverList.add(serverFound);
        update();
    }

    public void add(List<DATATYPE> serverFound) {
        serverList.addAll(serverFound);
    }

    public void remove(DATATYPE serverFound) {
        serverList.remove(serverFound);
        update();
    }

    public void onColumnClicked() {
        table.setOnMouseClicked(event -> {
            DATATYPE selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            IO.println(selected.toString());
        });
    }

    private void update() {
        table.setItems(serverList);
    }


}
