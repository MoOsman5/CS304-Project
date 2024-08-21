package ProjectJogal;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import java.awt.*;
import javax.swing.*;
import javax.media.opengl.*;
public class Main extends JFrame {
    private MainEventListener listener = new MainEventListener();
    GLCanvas gLCanvas;
    static FPSAnimator animator = null;
   
    
    public static void main(String[] args) {
      final Main app= new Main();
       
    }

    public Main() {
        setTitle("Chicken Egg");
        gLCanvas = new GLCanvas();
        gLCanvas.addGLEventListener(listener);
        gLCanvas.addKeyListener(listener);
        getContentPane().add(gLCanvas, BorderLayout.CENTER);
        animator = new FPSAnimator(gLCanvas, 10);
        animator.add(gLCanvas);

        animator.start();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);
        
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
        gLCanvas.requestFocus();
    }
}



