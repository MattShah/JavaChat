package GUI;


import Globals.GlobalVariables;
import SQL.SQLConnector;
import SQL.SQLResult;
//import Other.GlobalVariables;


import javax.print.attribute.standard.MediaSize;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JFrame{
    private JPanel panel1;
    private JButton loginButton;
    private JPasswordField passwordField;
    private JTextField loginField;
    private JLabel ErrorLabel;
    private JButton regButton;

    private boolean loginProcess = false;

    public  LoginPanel(){
        setTitle("Login Panel");
        setSize(600,500);
        add(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TryLogin();
            }
        });


        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterPanel registerPanel = new RegisterPanel();
                registerPanel.setVisible(true);
                setVisible(false);
                dispose();
            }
        });
    }

    private void TryLogin(){
        if(!loginProcess)
        new Thread(new Runnable() {
            @Override
            public void run() {
                loginProcess = true;
                if(SQLConnector.CheckConnection()) {
                    String nick = loginField.getText();
                    String password = passwordField.getText();
                    boolean exist = SQLConnector.CheckIfUserExist(nick, password);
                    String text = new String();
                    if (exist) {
                        LogIn(nick, password);
                        text = "No elo";
                    } else
                        text = "Incorrect nick or password";
                    ErrorLabel.setText(text);
                }
                else {
                    ErrorLabel.setText("Connection problem");
                }
                loginProcess = false;
            }
        }).start();
    }

    private void LogIn(String nick, String password){
        GlobalVariables.userID = SQLConnector.GetUserID(nick, password);
        GlobalVariables.userNick = nick;
        MainPanel mainPanel = new MainPanel();
        mainPanel.setVisible(true);
        setVisible(false);
        dispose();
    }

    void SetLoginField(String login){
        loginField.setText(login);
    }

    void SetErrorLabel(String message) {
        ErrorLabel.setText(message);
    }
}
