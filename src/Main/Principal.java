
package Main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import formularios.FormLogin;

public class Principal {

    public static void main(String[] args) {
         try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormLogin().setVisible(true);
            }
        });
    }
    
}
