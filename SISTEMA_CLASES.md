# Sistema de Clases - For The King Inspired

## Overview
Sistema completo de 5 clases de personajes con roles distintivos, equipamiento inicial, habilidades y sistema de loot con afinidad por clase.

## Las 5 Clases

### 1. GUARDIAN (Guarda del Alba) 🛡️
**Rol:** Tank / Control  
**Descripción:** Defensor inquebrantable que protege a sus aliados absorbiendo daño y controlando el campo de batalla.

**Stats Base:**
- HP: 100 ⭐ (El más tanque)
- Defensa: 15 ⭐
- Ataque: 10
- Velocidad: 3

**Equipamiento Inicial:**
1. Escudo simple (+3 DEF)
2. Armadura ligera (+5 DEF)
3. Maza básica (+8 ATK)

**Habilidades:**
- **Inicial:** Muralla Viviente - Aumenta DEF +10 por 2 turnos, atrae ataques enemigos
- **Nivel 2:** Golpe de Escudo - Ataque que aturde 1 turno
- **Nivel 3:** Protección Sagrada - Escudo mágico en aliado que absorbe 30 daño

**Loot Preferido:** Armaduras pesadas, escudos, mazas, anillos de mitigación

---

### 2. RANGER (Cazador Errante) 🏹
**Rol:** Ranged DPS / Explorador  
**Descripción:** Experto en ataques a distancia con gran movilidad y precisión mortal.

**Stats Base:**
- HP: 60
- Defensa: 8
- Ataque: 25 ⭐ (Máximo daño base)
- Velocidad: 8 ⭐

**Equipamiento Inicial:**
1. Arco corto (+10 ATK, Rango 4)
2. Capucha ligera (+2 DEF, +5% Evasión)
3. Flechas comunes (20 munición)

**Habilidades:**
- **Inicial:** Disparo Preciso - +15% crítico, +50% daño crítico por 3 turnos
- **Nivel 2:** Flecha Penetrante - Atraviesa enemigos en línea, 20 daño a todos
- **Nivel 3:** Paso Ligero - +3 velocidad, +15% evasión, puede atravesar enemigos

**Loot Preferido:** Arcos, ballestas, capas, amuletos de crítico

---

### 3. MAGE (Erudito Arcano) ✨
**Rol:** Mage / AOE / Control  
**Descripción:** Maestro de las artes arcanas que controla el campo con hechizos de área.

**Stats Base:**
- HP: 70
- Defensa: 8
- Ataque: 22
- Velocidad: 5

**Equipamiento Inicial:**
1. Bastón rústico (+12 ATK mágico, +5 Magic)
2. Túnica simple (+3 DEF, +10 Maná)
3. Grimorio básico (+1 ranura de hechizo)

**Habilidades:**
- **Inicial:** Chispa Arcana - AOE pequeña, 18 daño, -2 velocidad enemiga
- **Nivel 2:** Prisión de Energía - Crea zona que ralentiza 50% por 3 turnos
- **Nivel 3:** Descarga Rúnica - 45 daño masivo, -25% defensa enemiga

**Loot Preferido:** Bastones, tomos, cristales de maná, sombreros mágicos

---

### 4. CLERIC (Clérigo del Sendero) ⛪
**Rol:** Support / Healer  
**Descripción:** Sanador sagrado que mantiene vivo al grupo y proporciona bendiciones.

**Stats Base:**
- HP: 80
- Defensa: 12
- Ataque: 12
- Velocidad: 5

**Equipamiento Inicial:**
1. Cetro pequeño (+8 ATK, +8 Curación)
2. Escudo de madera (+3 DEF)
3. Vestimenta bendecida (+4 DEF, +5 HP)

**Habilidades:**
- **Inicial:** Rezo Curativo - Cura 25 HP a aliado, +5 DEF por 2 turnos
- **Nivel 2:** Luz Restauradora - AOE que cura 15 HP a todos los aliados
- **Nivel 3:** Bendición Firme - +8 DEF, +10 HP, inmunidad a debuffs por 3 turnos

**Loot Preferido:** Cetros sagrados, reliquias, vestimentas, tomos de bendiciones

---

### 5. ROGUE (Pícaro Sombrío) 🗡️
**Rol:** Stealth / Burst DPS  
**Descripción:** Asesino ágil que causa daño explosivo con críticos y trampas.

**Stats Base:**
- HP: 65
- Defensa: 7
- Ataque: 28 ⭐ (Máximo con críticos)
- Velocidad: 9 ⭐

**Equipamiento Inicial:**
1. Dagas simples (+12 ATK, +10% Crítico)
2. Guantes ágiles (+1 DEF, +1 SPD)
3. Kit de trampas básico (3 trampas)

**Habilidades:**
- **Inicial:** Golpe Sombra - Si no fue atacado, +100% daño + veneno 10/turno
- **Nivel 2:** Trampa de Espinas - Coloca trampa: 25 daño + inmoviliza 1 turno
- **Nivel 3:** Evasión Perfecta - Inmunidad a daño por 1 turno, próximo ataque +150%

**Loot Preferido:** Dagas, capas de sombras, kits de trampas, anillos de robo

---

## Sistema de Loot con Afinidad

### Mecánica Principal
Inspirado en **For The King**: el loot es compartido pero tiene **afinidad por clase**.

### Cómo Funciona
1. **60% de probabilidad** → Item específico para una clase del grupo
2. **40% de probabilidad** → Item genérico (pociones, pergaminos, recursos)

### Ejemplo de Grupo
Grupo: Guardian + Mage + Ranger

Cuando encuentran loot:
- Hay más probabilidad de encontrar: escudos, bastones, arcos
- Menos probable: cetros (no hay cleric), dagas (no hay rogue)
- Items genéricos siempre pueden aparecer

### Ventajas del Sistema
✅ **No frustrante:** Siempre hay posibilidad de encontrar tu equipo  
✅ **Variedad:** Items genéricos mantienen randomness  
✅ **Cooperativo:** El grupo se beneficia de composición balanceada  
✅ **KISS:** Regla simple del 60/40

---

## Fuentes de Loot

### ENEMY_COMMON
- 70% COMMON
- 25% UNCOMMON
- 5% RARE

### ENEMY_ELITE
- 40% UNCOMMON
- 40% RARE
- 15% EPIC
- 5% LEGENDARY

### CHEST
- 50% COMMON
- 30% UNCOMMON
- 15% RARE
- 5% EPIC

### EVENT
- 60% COMMON
- 30% UNCOMMON
- 10% RARE

---

## Integración con Sistemas Existentes

### ✅ Completado
1. **PlayerClass.java** - Enum con las 5 clases completas
2. **ClassAbilities.java** - 15 habilidades (3 por clase)
3. **InitialEquipment.java** - Equipamiento inicial por clase
4. **ClassBasedLootGenerator.java** - Generador con afinidad
5. **LootSystem.java** - Integrado con generador de afinidad
6. **Item.java** - Extendido con sistema de efectos

### 🔄 Pendiente de Integración
1. **GameState.java** - Usar PlayerClass.getMaxHP() para inicialización
2. **GameServer.java** - Reemplazar switch de baseDamage con PlayerClass.getBaseDamage()
3. **LobbyState.java** - Actualizar isValidClass() para usar PlayerClass.isValid()
4. **AbilitySystem.java** - Eliminar habilidades hardcodeadas, usar ClassAbilities

---

## Estructura de Archivos

```
src/main/java/com/juegito/
├── game/
│   ├── character/               [NUEVO PACKAGE]
│   │   ├── PlayerClass.java     ✨ 5 clases completas
│   │   ├── ClassAbilities.java  ✨ 15 habilidades
│   │   └── InitialEquipment.java ✨ Equipo inicial
│   │
│   └── loot/
│       ├── ClassBasedLootGenerator.java  ✨ Sistema de afinidad
│       ├── LootSystem.java              🔧 Integrado
│       └── Item.java                    🔧 Extendido con efectos
```

---

## Próximos Pasos

1. **Integrar PlayerClass en GameServer**
   - Usar PlayerClass.getBaseDamage() en combate
   - Usar PlayerClass.getMaxHP() al crear jugadores

2. **Actualizar AbilitySystem**
   - Eliminar habilidades hardcodeadas
   - Cargar desde ClassAbilities

3. **Lobby con Selección de Clases**
   - Permitir que jugadores elijan clase
   - Validar con PlayerClass.isValid()
   - Mostrar stats y habilidades

4. **Cliente: UI de Selección**
   - Pantalla de selección de clase en lobby
   - Mostrar stats, equipo inicial, habilidades
   - Preview visual de cada clase

5. **Sistema de Progresión**
   - Implementar desbloqueo de habilidades nivel 2 y 3
   - Sistema de experiencia
   - Mejorar equipamiento

---

## Diseño Inspirado en For The King

### Principios Aplicados
✅ **Roles Distintos:** Cada clase tiene propósito único  
✅ **Cooperación:** Grupo balanceado > 3 DPS  
✅ **Loot Compartido:** Evita competencia tóxica  
✅ **Afinidad de Loot:** Randomness con dirección  
✅ **Equipamiento Define Juego:** No solo stats, sino mecánicas  

### Diferencias con For The King
- **5 clases** (vs 12+) para mantener balance simple
- **Habilidades desbloqueables** (vs sistema de skills más complejo)
- **Sin permadeath** (más casual)
- **Turn-based puro** (sin dados en combate básico)

---

## Compilación

```bash
# Servidor
mvn clean compile -DskipTests

# Cliente
mvn -f client-pom.xml clean compile

# Ambos
BUILD SUCCESS ✅
```

---

## Créditos
Sistema diseñado siguiendo principios **KISS** y **DRY**.  
Inspirado en el diseño de clases de **For The King**.
