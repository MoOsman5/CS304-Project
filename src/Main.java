import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import java.awt.*;
//import java.awt.event.*;
import javax.swing.*;
import javax.media.opengl.*;
public class Main extends JFrame {

    private GLCanvas glcanvas;
    private MainEventListener listener = new MainEventListener();
    public static void main(String[] args) {
        Main app =new Main();
        final Animator animator = new Animator();
    }

    public Main() {

        super("Chicken Egg Game");
        final Animator animator;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        glcanvas = new GLCanvas();
        glcanvas.addGLEventListener(listener);
        animator = new FPSAnimator(20);
        animator.add(glcanvas);
        animator.start();
        getContentPane().add(glcanvas, BorderLayout.CENTER);
        setSize(1000, 700);
        setLocationRelativeTo(this);
        setVisible(true);
    }
}



