package chat.frame;

import chat.config.BeanFactory;
import chat.web.controller.ChatController;
import chat.web.model.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.TRAILING;

public class LoginFrame extends JFrame {
    private static final String RESPONSE_KEY = "Response";
    private static final String OK = "Ok";
    private static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    private static final String NOT_AUTHENTICATED = "Not authenticated";
    private static final String NOT_FOUND = "Not found";

    private final ChatController chatController = BeanFactory.chatController();
    private final ChatFrame chatFrame = BeanFactory.chatFrame();

    private JPanel jPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private String username;

    public LoginFrame() {
        initUI();
    }

    public void onAuthenticationAction(Message message) {
        System.out.println("on action called");
        String authResponse = message.headers().get(RESPONSE_KEY);
        System.out.println(authResponse);
        if (authResponse.equals(OK)) {
            chatFrame.setUsername(username);
            chatController.getPersonList(response -> chatFrame.onReceivedPersonListAction(response));
            chatFrame.setVisible(true);
            setVisible(false);
        } else if(authResponse.equals(NOT_FOUND) ||
        authResponse.equals(NOT_AUTHENTICATED)) {
            showWarning("Wrong username or password. Please try again");
        } else if(authResponse.equals(USERNAME_ALREADY_EXISTS)){
            showWarning("Sorry this username is already registered. Please try something different");
        }
    }

    private void initUI() {

        var userNameLbl = new JLabel("User name:");
        var passwordLbl = new JLabel("Password:");

        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);

        var loginButton = new JButton("Log in");
        loginButton.addActionListener(new LogInAction());
        var signupButton = new JButton("Sign up");
        signupButton.addActionListener(new SignUpAction());

        createLayout(userNameLbl, usernameField, passwordLbl, passwordField, loginButton, signupButton);

        setTitle("Welcome to chat!");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private class SignUpAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            doSignUpAction();
        }

        private void doSignUpAction() {
            username = usernameField.getText();
            var password = passwordField.getPassword();

            if (!username.isEmpty() && password.length != 0) {

                chatController.register(username, new String(password),
                        LoginFrame.this::onAuthenticationAction);
            }
        }
    }

    private class LogInAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            doLoginAction();
        }

        private void doLoginAction() {
            username = usernameField.getText();
            var password = passwordField.getPassword();

            if (!username.isEmpty() && password.length != 0) {

                chatController.logIn(username, new String(password),
                        LoginFrame.this::onAuthenticationAction);
            }
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(jPanel, message, "Warning", JOptionPane.WARNING_MESSAGE);
        usernameField.setText("");
        passwordField.setText("");
    }

    private void createLayout(
            JLabel userNameLbl, JTextField usernameField, JLabel passwordLbl, JPasswordField passwordField,
            JButton loginButton, JButton signupButton) {
        var pane = getContentPane();
        var gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(TRAILING)
                        .addComponent(userNameLbl)
                        .addComponent(passwordLbl))
                .addGroup(gl.createParallelGroup(TRAILING)
                        .addComponent(usernameField)
                        .addComponent(passwordField)
                        .addComponent(loginButton))
                        .addComponent(signupButton)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(BASELINE)
                        .addComponent(userNameLbl)
                        .addComponent(usernameField))
                .addGroup(gl.createParallelGroup(BASELINE)
                        .addComponent(passwordLbl)
                        .addComponent(passwordField))
                .addGroup(gl.createParallelGroup(BASELINE)
                        .addComponent(loginButton)
                        .addComponent(signupButton))
        );

        pack();
    }
}
