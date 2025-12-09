package com.juegito.client.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.juegito.protocol.dto.character.AbilityInfoDTO;
import com.juegito.protocol.dto.character.ClassInfoDTO;

import java.util.List;
import java.util.function.Consumer;

/**
 * Panel de selección de clase para el lobby.
 */
public class ClassSelectorPanel {
    private final Table container;
    private final Skin skin;
    private final List<ClassInfoDTO> availableClasses;
    private final Consumer<ClassInfoDTO> onClassSelected;
    
    private ClassInfoDTO selectedClass;
    
    public ClassSelectorPanel(Skin skin, List<ClassInfoDTO> classes, Consumer<ClassInfoDTO> onClassSelected) {
        if (skin == null) {
            throw new IllegalArgumentException("Skin cannot be null");
        }
        if (onClassSelected == null) {
            throw new IllegalArgumentException("onClassSelected callback cannot be null");
        }
        
        this.skin = skin;
        this.availableClasses = classes != null ? classes : new java.util.ArrayList<>();
        this.onClassSelected = onClassSelected;
        this.container = new Table();
        
        buildUI();
    }
    
    private void buildUI() {
        container.top().left();
        container.defaults().pad(5).left();
        
        Label title = new Label("Selecciona tu clase", skin);
        title.setFontScale(1.5f);
        container.add(title).colspan(2).center().padBottom(10).row();
        
        // Crear scroll pane para la lista de clases
        Table classesList = new Table();
        classesList.defaults().pad(5).expandX().fillX();
        
        if (availableClasses.isEmpty()) {
            Label emptyLabel = new Label("No hay clases disponibles aun.\nEsperando al servidor...", skin);
            emptyLabel.setWrap(true);
            classesList.add(emptyLabel).width(300).center();
        } else {
            for (ClassInfoDTO classInfo : availableClasses) {
                if (classInfo != null) {
                    classesList.add(createClassCard(classInfo)).row();
                }
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(classesList, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        
        container.add(scrollPane).expand().fill().colspan(2).row();
    }
    
    private Table createClassCard(ClassInfoDTO classInfo) {
        Table card = new Table();
        
        // Intentar obtener background, si falla usar sin background
        try {
            if (skin.has("window", com.badlogic.gdx.scenes.scene2d.utils.Drawable.class)) {
                card.setBackground(skin.getDrawable("window"));
            }
        } catch (Exception e) {
            // Ignorar si no hay drawable, continuar sin background
        }
        
        card.defaults().pad(3).left();
        
        // Nombre y rol
        String displayName = classInfo.getDisplayName() != null ? classInfo.getDisplayName() : "Clase Desconocida";
        Label nameLabel = new Label(displayName, skin);
        nameLabel.setFontScale(1.2f);
        card.add(nameLabel).colspan(2).left().row();
        
        String role = classInfo.getRole() != null ? classInfo.getRole() : "";
        Label roleLabel = new Label(role, skin);
        roleLabel.setColor(0.7f, 0.7f, 0.7f, 1f);
        card.add(roleLabel).colspan(2).left().padBottom(5).row();
        
        // Descripción
        String description = classInfo.getDescription() != null ? classInfo.getDescription() : "Sin descripción";
        Label descLabel = new Label(description, skin);
        descLabel.setWrap(true);
        card.add(descLabel).width(300).colspan(2).left().padBottom(5).row();
        
        // Stats
        if (classInfo.getBaseStats() != null) {
            Table statsTable = new Table();
            statsTable.defaults().pad(2);
            statsTable.add(new Label("HP: " + classInfo.getBaseStats().getHp(), skin));
            statsTable.add(new Label("ATK: " + classInfo.getBaseStats().getAttack(), skin));
            statsTable.add(new Label("DEF: " + classInfo.getBaseStats().getDefense(), skin));
            statsTable.add(new Label("SPD: " + classInfo.getBaseStats().getSpeed(), skin));
            card.add(statsTable).colspan(2).left().padBottom(5).row();
        }
        
        // Equipo inicial
        if (classInfo.getInitialEquipment() != null && !classInfo.getInitialEquipment().isEmpty()) {
            Label equipLabel = new Label("Equipo: " + String.join(", ", classInfo.getInitialEquipment()), skin);
            equipLabel.setWrap(true);
            equipLabel.setFontScale(0.9f);
            card.add(equipLabel).width(300).colspan(2).left().padBottom(5).row();
        }
        
        // Habilidad inicial
        if (classInfo.getInitialAbility() != null) {
            AbilityInfoDTO ability = classInfo.getInitialAbility();
            Label abilityLabel = new Label("Inicial: " + ability.getName(), skin);
            abilityLabel.setColor(0.2f, 1f, 0.2f, 1f);
            card.add(abilityLabel).left().row();
            
            Label abilityDesc = new Label(ability.getDescription(), skin);
            abilityDesc.setFontScale(0.85f);
            abilityDesc.setWrap(true);
            card.add(abilityDesc).width(300).left().padBottom(5).row();
        }
        
        // Habilidades desbloqueables
        if (classInfo.getUnlockableAbilities() != null && !classInfo.getUnlockableAbilities().isEmpty()) {
            Label unlockLabel = new Label("Desbloqueables:", skin);
            unlockLabel.setColor(1f, 1f, 0.5f, 1f);
            card.add(unlockLabel).colspan(2).left().row();
            
            for (AbilityInfoDTO ability : classInfo.getUnlockableAbilities()) {
                Label abName = new Label("• " + ability.getName(), skin);
                abName.setFontScale(0.85f);
                card.add(abName).left().row();
                
                Label abDesc = new Label("  " + ability.getDescription(), skin);
                abDesc.setFontScale(0.75f);
                abDesc.setWrap(true);
                card.add(abDesc).width(280).left().row();
            }
        }
        
        // Botón de selección
        TextButton selectButton = new TextButton("Seleccionar", skin);
        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedClass = classInfo;
                onClassSelected.accept(classInfo);
            }
        });
        
        card.add(selectButton).colspan(2).center().padTop(10).row();
        
        return card;
    }
    
    public Table getContainer() {
        return container;
    }
    
    public ClassInfoDTO getSelectedClass() {
        return selectedClass;
    }
}
