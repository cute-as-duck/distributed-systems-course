package chat.frame;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileExplorerTableFrame extends JFrame {

    private final ChatFrame.SendFileAction sendFile;

    private List<Path> paths = new ArrayList<>();
    private FileTableModel model;
    private JTable table;
    private JScrollPane jScrollPane;
    private JButton backBtn = new JButton("Back");
    private JButton sendBtn = new JButton("Send");
    private JButton cancelBtn = new JButton("Cancel");

    private Path path = Paths.get(System.getProperty("user.home"));

    public FileExplorerTableFrame(ChatFrame.SendFileAction sendFile) {
        this.sendFile = sendFile;
        initUI();
    }

    private void initUI() {
        paths = getPathsFromPath(path);

        model = new FileTableModel(paths);
        table = new JTable(model);
        jScrollPane = new JScrollPane(table);

        backBtn.addActionListener(new BackBtnAction());
        sendBtn.addActionListener(new SendBtnAction());

        createLayout();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    String value = table.getModel().getValueAt(selectedRow, 0).toString();
                    if (table.getModel().getValueAt(selectedRow, 1).toString().equals("D")) {
                        path = path.resolve(value);
                        paths = getPathsFromPath(path);
                        model.updateData(paths);
                    }
                }
            }
        });
    }

    private List<Path> getPathsFromPath(Path path) {
        String[] files = path.toFile().list();
        List<Path> paths = new ArrayList<>();
        Path constructedPath;
        for (String file : files) {
            if (file.charAt(0) != '.') {
                constructedPath = path.resolve(Paths.get(file));
                paths.add(constructedPath);
            }
        }
        return paths;
    }

    private void createLayout() {
        var pane = getContentPane();
        var gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(backBtn)
                .addComponent(jScrollPane)
                .addGroup(gl.createSequentialGroup()
                        .addComponent(sendBtn)
                        .addComponent(cancelBtn)));

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(backBtn)
                .addComponent(jScrollPane)
                .addGroup(gl.createParallelGroup()
                        .addComponent(sendBtn)
                        .addComponent(cancelBtn)));

        pack();
    }

    private class FileTableModel extends AbstractTableModel {
        private List<Path> paths;

        FileTableModel(List<Path> paths) {
            this.paths = paths;
        }

        @Override
        public int getRowCount() {
            return paths.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return paths.get(rowIndex).getFileName();
            } else {
                return Files.isDirectory(paths.get(rowIndex)) ? "D" : "F";
            }
        }

        public void updateData(List<Path> paths) {
            this.paths = paths;
            fireTableStructureChanged();
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) {
                return "Name";
            } else {
                return "Kind";
            }
        }
    }

    private class SendBtnAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            String fileName = table.getModel().getValueAt(selectedRow, 0).toString();
            Path file = path.resolve(fileName);
            byte[] content = null;
            try {
                content = Files.readAllBytes(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            sendFile.fileSelected(fileName, content);
        }
    }

    private class BackBtnAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            Path parentPath = path.getParent();
            if (parentPath != null) {
                paths = getPathsFromPath(parentPath);
                path = parentPath;
                model.updateData(paths);
            }
        }
    }
}
