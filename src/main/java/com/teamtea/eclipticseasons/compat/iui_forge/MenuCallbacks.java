package com.teamtea.eclipticseasons.compat.iui_forge;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.anningui.iui_forge.render.Element;
import org.anningui.iui_forge.render.elements.Button;
import org.anningui.iui_forge.script.CallbackHandler;
import org.anningui.iui_forge.script.CallbackListener;
import org.anningui.iui_forge.script.events.KeyEvent;
import org.anningui.iui_forge.script.events.MouseEvent;

import java.util.List;

public class MenuCallbacks implements CallbackListener {

    @CallbackHandler
    public void handleMouseCallbacks(MouseEvent e) {
        switch (e.input) {
            case CLICK -> onClick(e);
            case RELEASE -> onRelease(e);
        }
        EclipticSeasons.logger(e.target.innerText);
    }

    @CallbackHandler
    public void handleKeyEventCallbacks(KeyEvent e) {
        EclipticSeasons.logger(e.key);
        Element parent = e.target.parent;
        Button button = new Button();
        parent.innerText = "ss";
        parent.addChild(button);
    }

    public void onClick(MouseEvent e) {
        Element bti = e.target.parentPanel.collectFirstById("bti");
        if (e.target.classList.contains("temp")) {
            ResourceLocation id = new ResourceLocation(e.target.getTag());
            bti.innerText = id.toString();
            return;
        }

        Element parent = e.target.parent;
        Element dd = e.target.parentPanel.collectFirstById("dd");
        try {
            List<Element> temp = e.target.parentPanel.collectByClassAttribute("temp");
            for (Element element : temp) {
                dd.removeChild(element);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            var list = ClientCon.getUseLevel().registryAccess().registryOrThrow(Registries.BIOME).keySet().
                    stream().toList();


            int c = 0;
            for (var location : list) {
                String biomeName = Component.translatable(Util.makeDescriptionId("biome", location)).getString();

                if (location.toString().contains(bti.innerText)
                        || biomeName.contains(bti.innerText)) {
                    c++;
                    Button button = new Button();
                    button.queueProperty("size: 30% 10");
                    button.classList.add("temp");
                    button.callAttribute("#temp1");
                    button.callAttribute("-temp");
                    button.queueProperty("text-color: red");
                    button.queueProperty("inner-text: \"%s\"".formatted(biomeName));
                    button.callAttribute(location.toString());
                    button.clickAction = ("handleMouseCallbacks");
                    button.parentPanel = parent.parentPanel;
                    dd.addChild(button);

                }
                if (c > 5) break;
            }

            dd.parent.parent.style();
        } catch (Exception ex) {

        }
    }

    public void onRelease(MouseEvent e) {

    }
}
