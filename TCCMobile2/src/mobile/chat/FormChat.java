package mobile.chat;

import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.layouts.BoxLayout;
import java.io.IOException;
import mobile.ui.*;

public class FormChat extends BaseForm {

    public void cleanup() {
    }

   public String getName() {
       return "Fonts";
   }

   protected String getHelp() {
       return "The toolkit has support for custom fonts and system/device fonts " +
           "both are fully interchangable and seamless for the developer.";
   }

   protected void execute(Form f) {
       f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
       f.addComponent(createFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM), "System Font"));
       f.addComponent(createFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD | Font.STYLE_ITALIC, Font.SIZE_LARGE), "Bold Italic Large System Font"));
//       f.addComponent(createFont(Font.getBitmapFont("Dialog"), "Dialog 12 Anti-Aliased Bitmap Font"));
//       f.addComponent(createFont(Font.getBitmapFont("DialogInput"), "DialogInput 12 Anti-Aliased Bitmap Font"));
//       f.addComponent(createFont(Font.getBitmapFont("SansSerif"), "SansSerif 20 Anti-Aliased Bitmap Font"));
//       f.addComponent(createFont(Font.getBitmapFont("Serif"), "Serif 12 Bold Anti-Aliased Bitmap Font"));
//       f.addComponent(createFont(Font.getBitmapFont("Monospaced"), "Monospaced 10 Anti-Aliased Bitmap Font"));
//       Label l = createFont(Font.getBitmapFont("Dialog"), "Dialog 12 Bitmap Font in Red");
//       l.getStyle().setFgColor(0xff0000);
//       f.addComponent(l);
   }

   private Label createFont(Font f, String label) {
       Label fontLabel = new Label(label);
       fontLabel.getStyle().setFont(f);
       fontLabel.setFocusable(true);
       //fontLabel.setFocusPainted(false);
       fontLabel.getStyle().setBgTransparency(0);
       return fontLabel;
   }
   
}
