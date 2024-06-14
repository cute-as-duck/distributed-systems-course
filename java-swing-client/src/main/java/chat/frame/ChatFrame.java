package chat.frame;

import chat.config.BeanFactory;
import chat.dto.SendingFile;
import chat.web.controller.ChatController;
import chat.dto.PersonListResponse;
import chat.dto.ChatMessage;
import chat.dto.Person;
import chat.web.model.Message;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFrame extends JFrame {

    private final ChatController chatController = BeanFactory.chatController();

    private String username;
    private DefaultListModel<String> listModel;
    private JList<String> list;
    private JLabel contactLbl = new JLabel();
    private JScrollPane contactsPane;
    private JScrollPane dialoguePane;
    private JTextField messageField = new JTextField(10);
    private JTextArea textArea = new JTextArea();
    private JButton sendButton;
    private JButton sendFileBtn;
    private Map<String, List<ChatMessage>> messageHistories = new HashMap<>();

    private List<Person> persons;

    public ChatFrame() {
        initUI();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void onAddPersonAction(Person person) {
        listModel.addElement(person.username());
        persons.add(person);
    }

    public void onRemovePersonAction(String username) {
        listModel.removeElement(username);
        Person person = persons.stream().filter(p -> p.username().equals(username)).findFirst().orElseThrow();
        persons.remove(person);
    }

    public void onReceivedPersonListAction(Message message) {
        System.out.println("On set users action called...");
        persons = PersonListResponse.fromJson(message.body()).persons();

        persons.stream().forEach(p -> System.out.println(p.username()));
        List<String> names = persons.stream().map(p -> p.username()).filter(name -> !name.equals(username)).toList();
        listModel.removeAllElements();
        listModel.addAll(names);
    }

    public void onReceiveMessageAction(ChatMessage chatMessage) {
        System.out.println("On received message called");
        if (!messageHistories.containsKey(chatMessage.senderName())) {
            messageHistories.put(chatMessage.senderName(), new ArrayList<>());
        }
        messageHistories.get(chatMessage.senderName()).add(new ChatMessage(chatMessage.senderName(), chatMessage.message()));
        if (contactLbl.getText().equals(chatMessage.senderName())) {
            textArea.append(chatMessage.senderName() + ": " + chatMessage.message() + "\r\n");
            textArea.setCaretPosition(textArea.getText().length());
        }
    }

    public void onReceiveFileAction(SendingFile sendingFile) {
        try {
            Files.writeString(
                    Paths.get(System.getProperty("user.home")).resolve("Downloads").resolve(sendingFile.fileName()),
                    sendingFile.content());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!messageHistories.containsKey(sendingFile.senderName())) {
            messageHistories.put(sendingFile.senderName(), new ArrayList<>());
        }
        String chatMessage = String.format("%s sent file '%s'", sendingFile.senderName(), sendingFile.fileName());
        messageHistories.get(sendingFile.senderName()).add(new ChatMessage(sendingFile.senderName(), chatMessage));
        if (contactLbl.getText().equals(sendingFile.senderName())) {
            textArea.append(chatMessage + "\r\n");
            textArea.setCaretPosition(textArea.getText().length());
        }
    }

    private void initUI() {

        createMenuBar();

        //textArea
        dialoguePane = new JScrollPane(textArea);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        //List of persons
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.addListSelectionListener(new ListSelectionHandler());
        contactsPane = new JScrollPane(list);

        sendFileBtn = new JButton("Send file");
        sendFileBtn.addActionListener(new SendFileAction());

        sendButton = new JButton("Send message");
        sendButton.addActionListener(new SendButtonAction());

        createLayout();

        setTitle(username);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                chatController.exitChat(username);
                System.out.println("Closing...");
            }
        });
    }

    private void createMenuBar() {
        var menuBar = new JMenuBar();
        var menu = new JMenu("Menu");

        var exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(event -> {
                    chatController.exitChat(username);
                    System.exit(0);
                });

        menu.add(exitMenuItem);
        menuBar.add(menu);

        setJMenuBar(menuBar);
    }

    private void createLayout() {
        var pane = getContentPane();
        var gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addComponent(contactsPane)
                .addGroup(gl.createParallelGroup()
                        .addComponent(contactLbl)
                        .addComponent(dialoguePane)
                        .addGroup(gl.createSequentialGroup()
                                .addComponent(messageField)
                                .addGroup(gl.createParallelGroup()
                                        .addComponent(sendFileBtn)
                                        .addComponent(sendButton))))
        );

        gl.setVerticalGroup(gl.createParallelGroup()
                .addComponent(contactsPane)
                .addGroup(gl.createSequentialGroup()
                        .addComponent(contactLbl)
                        .addComponent(dialoguePane)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(messageField)
                                .addGroup(gl.createSequentialGroup()
                                        .addComponent(sendFileBtn)
                                        .addComponent(sendButton)))));

        pack();
    }

    private class ListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            textArea.setText("");
            String name = list.getSelectedValue();
            contactLbl.setText(name);
            if (!messageHistories.containsKey(name)) {
                messageHistories.put(name, new ArrayList<>());

            } else {
                messageHistories.get(name).stream()
                        .map(message -> "%s: %s\r\n".formatted(message.senderName(), message.message()))
                        .forEach(message -> textArea.append(message));
                textArea.setCaretPosition(textArea.getText().length());
            }
        }
    }

    public class SendFileAction extends AbstractAction {

        private Person person;
        private FileExplorerTableFrame fileExplorer;

        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedName = contactLbl.getText();
            person = persons.stream().filter(p -> p.username().equals(selectedName)).findFirst().orElseThrow();
            fileExplorer = new FileExplorerTableFrame(this);
            fileExplorer.setVisible(true);
        }

        public void fileSelected(String fileName, byte[] content) {
            fileExplorer.setVisible(false);
            chatController.sendFile(person, fileName, content);
            String chatMessage = String.format("%s sent file '%s'",username, fileName);
            textArea.append(chatMessage + "\r\n");
            textArea.setCaretPosition(textArea.getText().length());
            messageHistories.get(person.username()).add(new ChatMessage(username, chatMessage));
        }
    }

    private class SendButtonAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedName = contactLbl.getText();
            Person person = persons.stream().filter(p -> p.username().equals(selectedName)).findFirst().orElseThrow();
            String message = messageField.getText();
            chatController.sendMessage(person, new ChatMessage(username, message));
            textArea.append(username + ": " + message + "\r\n");
            textArea.setCaretPosition(textArea.getText().length());
            messageField.setText("");

            messageHistories.get(selectedName).add(new ChatMessage(username, message));
        }
    }
}
