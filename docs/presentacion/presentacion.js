const slides = [...document.querySelectorAll(".slide")];
const counter = document.getElementById("counter");
const bar = document.getElementById("bar");
let index = 0;

function show(next) {
  index = Math.max(0, Math.min(slides.length - 1, next));
  slides.forEach((slide, i) => slide.classList.toggle("active", i === index));
  counter.textContent = `${String(index + 1).padStart(2, "0")} / ${String(slides.length).padStart(2, "0")}`;
  bar.style.width = `${((index + 1) / slides.length) * 100}%`;
  document.querySelectorAll(".thumb").forEach((thumb, i) => thumb.classList.toggle("on", i === index));
  const hash = `#${index + 1}`;
  if (location.hash !== hash) history.replaceState(null, "", hash);
}

document.getElementById("next").onclick = () => show(index + 1);
document.getElementById("prev").onclick = () => show(index - 1);

document.getElementById("btn-notes").onclick = () => {
  document.body.classList.toggle("notes-on");
  document.getElementById("btn-notes").classList.toggle("active", document.body.classList.contains("notes-on"));
};
document.getElementById("btn-overview").onclick = () => document.body.classList.toggle("overview-on");

const grid = document.getElementById("overview-grid");
slides.forEach((slide, i) => {
  const button = document.createElement("button");
  button.className = "thumb";
  button.innerHTML = `<small>${String(i + 1).padStart(2, "0")}</small><strong>${slide.dataset.title}</strong>`;
  button.onclick = () => {
    document.body.classList.remove("overview-on");
    show(i);
  };
  grid.appendChild(button);
});

document.addEventListener("keydown", (event) => {
  const typing = event.target.matches("input, textarea, select");
  if (event.key === "Escape") {
    document.body.classList.remove("overview-on");
    return;
  }
  if (typing) return;
  if (event.key === "ArrowRight" || event.key === " " || event.key === "PageDown") {
    event.preventDefault();
    show(index + 1);
  } else if (event.key === "ArrowLeft" || event.key === "PageUp") {
    event.preventDefault();
    show(index - 1);
  } else if (event.key === "Home") show(0);
  else if (event.key === "End") show(slides.length - 1);
  else if (event.key.toLowerCase() === "n") document.getElementById("btn-notes").click();
  else if (event.key.toLowerCase() === "o") document.body.classList.toggle("overview-on");
});

let touchX = null;
document.addEventListener("touchstart", (event) => { touchX = event.changedTouches[0].clientX; }, { passive: true });
document.addEventListener("touchend", (event) => {
  if (touchX == null) return;
  const delta = event.changedTouches[0].clientX - touchX;
  if (Math.abs(delta) > 50) show(index + (delta < 0 ? 1 : -1));
  touchX = null;
}, { passive: true });

const plans = {
  beginner: ["Cuerpo completo A", "Cardio y core", "Tren superior", "Descanso activo", "Tren inferior", "Core y movilidad", "Descanso"],
  intermediate: ["Pecho", "Espalda", "Pierna", "Hombro", "Brazo", "Full body y core", "Descanso"],
  advanced: ["Pecho y tríceps", "Espalda y bíceps", "Pierna pesada", "Hombro y core", "Brazo y antebrazo", "Volumen extra", "Descanso"],
};
const days = ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"];

function renderWeek() {
  const level = document.getElementById("level").value;
  const goal = document.getElementById("goal").value;
  const titles = plans[level].slice();
  if ((goal === "lose_fat" || goal === "endurance") && level === "beginner") titles[5] = "Core y cardio";
  if ((goal === "lose_fat" || goal === "endurance") && level !== "beginner") {
    titles[5] = level === "advanced" ? "Volumen + cardio" : "Full body y cardio";
  }
  const counts = { beginner: [5, 4, 5, 0, 5, 4, 0], intermediate: [4, 4, 5, 4, 5, 4, 0], advanced: [6, 6, 6, 6, 6, 6, 0] }[level];
  document.getElementById("week").innerHTML = `<div class="grid-4">${titles.map((title, i) =>
    `<div class="step" style="grid-template-columns:36px 1fr"><b>${days[i][0]}</b><div>${title}<small>${counts[i] ? counts[i] + " ejercicios" : "recuperación"}</small></div></div>`
  ).join("")}</div>`;
  const reps = goal === "lose_fat" || goal === "endurance" ? "12-15 reps · descanso corto" : "compuesto en 6-8 · descanso hasta 120 s";
  document.getElementById("week-note").textContent = `toExercise aplicaría: ${reps}. Semilla estable mientras no cambie el perfil.`;
}
document.getElementById("level").onchange = renderWeek;
document.getElementById("goal").onchange = renderWeek;
renderWeek();

const athlete = () => ({
  age: Number(document.getElementById("age").value),
  w: Number(document.getElementById("weight").value),
  h: Number(document.getElementById("height").value),
  level: document.getElementById("level").value,
  goal: document.getElementById("goal").value,
});

function metrics(p) {
  const meters = p.h / 100;
  const bmi = Math.round((p.w / (meters * meters)) * 10) / 10;
  const label = bmi < 18.5 ? "Bajo" : bmi < 25 ? "Normal" : bmi < 30 ? "Alto" : "Muy alto";
  const base = (10 * p.w) + (6.25 * p.h) - (5 * p.age) + 5;
  const factor = { beginner: 1.375, intermediate: 1.55, advanced: 1.725 }[p.level];
  const shift = { lose_fat: -350, gain_muscle: 300, maintain: 0, endurance: 150 }[p.goal];
  const kcal = Math.round((base * factor + shift) / 10) * 10;
  const protein = Math.round(p.w * (p.goal === "gain_muscle" ? 2 : p.goal === "lose_fat" ? 2.2 : 1.7));
  return { bmi, label, kcal, protein };
}

function renderDiet() {
  const p = athlete();
  document.getElementById("wlab").textContent = `${p.w} kg`;
  document.getElementById("hlab").textContent = `${p.h} cm`;
  document.getElementById("alab").textContent = String(p.age);
  const m = metrics(p);
  document.getElementById("bmi").textContent = `${m.bmi} · ${m.label}`;
  document.getElementById("kcal").textContent = `${m.kcal} kcal`;
  document.getElementById("protein").textContent = `${m.protein} g`;
  const slots = [["Desayuno", 0.26], ["Almuerzo", 0.36], ["Snack", 0.12], ["Cena", 0.26]];
  document.getElementById("split-kcal").textContent = slots
    .map(([name, share]) => `${name} ${Math.round(m.kcal * share)} kcal`)
    .join(" · ");
}
["weight", "height", "age"].forEach((id) => document.getElementById(id).oninput = renderDiet);
document.getElementById("level").addEventListener("change", renderDiet);
document.getElementById("goal").addEventListener("change", renderDiet);
renderDiet();

function coachAnswer(question) {
  const p = athlete();
  const m = metrics(p);
  const text = question.toLowerCase()
    .replaceAll("á", "a").replaceAll("é", "e").replaceAll("í", "i")
    .replaceAll("ó", "o").replaceAll("ú", "u").replaceAll("ñ", "n");
  const intents = [
    { keys: ["proteina", "proteinas", "protein"], reply: () => {
      const low = Math.round(p.w * 1.6);
      const high = Math.round(p.w * 2.2);
      let line = `Con tus ${p.w} kg, la proteína diaria va de ${low} g a ${high} g. Tu meta de perfil, según el objetivo, está en ${m.protein} g.`;
      if (p.goal === "lose_fat") line += " Para bajar grasa, quédate en la parte alta del rango.";
      return line;
    } },
    { keys: ["calor", "deficit", "cuanto comer"], reply: () => `Tu objetivo está en ${m.kcal} kcal al día. El plan de Dieta ya reparte esa cifra en cuatro comidas.` },
    { keys: ["imc", "peso"], reply: () => `Tu IMC es ${m.bmi} (${m.label.toLowerCase()}), con ${p.w} kg y ${p.h} cm. El IMC no distingue músculo de grasa.` },
    { keys: ["cardio", "pesas"], reply: () => ({
      gain_muscle: "Pesas primero y cardio al final: tu estímulo principal son las series pesadas.",
      lose_fat: "El déficit manda. Aun así, pesas primero para conservar músculo y 15-20 min de cardio al cerrar.",
      endurance: "El trabajo aeróbico de calidad va primero y la fuerza después, como soporte.",
      maintain: "Calienta, haz pesas y cierra con 15-20 minutos suaves.",
    })[p.goal] },
    { keys: ["agua", "hidrat"], reply: () => `Unos ${(p.w * 35 / 1000).toFixed(1)} litros al día, más 500-800 ml por cada hora de entrenamiento.` },
    { keys: ["dormir", "sueno"], reply: () => "Apunta a 7-9 horas. Con menos de 6 sube el hambre y baja el rendimiento. Ningún suplemento reemplaza eso." },
    { keys: ["sentadilla", "squat"], reply: () => "Revisa tobillo, rodillas hacia afuera y profundidad hasta antes del butt wink. El artículo está en Aprender." },
    { keys: ["rutina", "entreno"], reply: () => "Tu rutina de gym usa solo el equipo que marcaste. Ábrela en la pestaña Rutina y marca cada ejercicio: el descanso arranca solo." },
  ];
  let best = null;
  let score = 0;
  for (const intent of intents) {
    const hits = intent.keys.filter((key) => text.includes(key)).length;
    if (hits > score) { score = hits; best = intent; }
  }
  if (!best) return "Puedo afinar proteína, calorías, IMC, cardio, hidratación, sueño, sentadilla o tu rutina. Pregunta por una de esas.";
  return best.reply();
}

const chat = document.getElementById("chat");
function pushBubble(kind, text) {
  const div = document.createElement("div");
  div.className = `bubble ${kind}`;
  div.textContent = text;
  chat.appendChild(div);
  chat.scrollTop = chat.scrollHeight;
}
function ask(question) {
  const clean = question.trim();
  if (!clean) return;
  pushBubble("user", clean);
  pushBubble("bot", coachAnswer(clean));
  document.getElementById("q").value = "";
}
document.getElementById("ask").onclick = () => ask(document.getElementById("q").value);
document.getElementById("q").addEventListener("keydown", (event) => {
  if (event.key === "Enter") ask(event.target.value);
});
["¿Cuántas proteínas necesito?", "¿Cardio antes o después?", "¿Cuánto debería comer?"].forEach((label) => {
  const button = document.createElement("button");
  button.type = "button";
  button.textContent = label;
  button.onclick = () => ask(label);
  document.getElementById("suggestions").appendChild(button);
});
pushBubble("bot", "Soy el coach local de esta presentación. Uso el peso de Dieta y el objetivo elegido en Rutina.");

const layers = {
  "Arranque": [
    ["FormaApp", "Pinta Loading, Auth, Onboarding o MainNavigation según RootState."],
    ["RootViewModel", "Observa la sesión y el perfil para decidir la puerta de entrada."],
    ["AuthViewModel", "toggleMode, onName, onEmail, onPassword, submit, signInWithGoogle."],
    ["OnboardingViewModel", "prefill, medidas, onSport, toggleEquipment, toggleIngredient, next."],
    ["navigateToTab", "Cambia de pestaña guardando y restaurando el back stack."],
  ],
  "Pantallas": [
    ["HomeViewModel", "todayWeekDay, greeting, retry. Combina perfil, stats, artículos y sesión."],
    ["RoutineViewModel", "load, selectDay, toggleExercise, resetDay, startTimer, stopTimer."],
    ["FinishWorkoutViewModel", "onPhoto, onCaption, toggleShare, save."],
    ["DietViewModel", "reload, selectDay, toggleOptions, chooseOption."],
    ["RecipeDetailViewModel", "onPlatePhoto."],
    ["IngredientsViewModel", "toggle, save. Edita los alimentos que gustan."],
    ["CommunityViewModel", "selectSport, toggleLike, react, toggleFollow."],
    ["AiViewModel", "onInput, send, clear."],
    ["LearnViewModel", "selectCategory, onQueryChange, toggleOnlySaved, toggleSaved."],
    ["ProfileViewModel", "updatePhoto, updateBody, updateGoal, setRestSeconds, setReminders, signOut."],
  ],
  "Repositorios": [
    ["AuthRepository", "signInWithEmail, signUpWithEmail, signInWithGoogle, signOut."],
    ["ProfileRepository", "current, save, updatePhoto, clear."],
    ["TrainingRepository", "weeklyRoutine, setExerciseCompleted, resetDay, sessions, stats, finishWorkout."],
    ["NutritionRepository", "dayPlan, chooseOption, setPlatePhoto, ingredientCatalog."],
    ["CommunityRepository", "feed, myPosts, toggleLike, react, toggleFollow, publish, syncMeWithProfile."],
    ["LearnRepository", "articles, article, recommended, toggleSaved."],
    ["ChatRepository", "messages, send, clear, suggestedQuestions."],
    ["AiRepository", "generateWeeklyRoutine, generateDayMeals, answer, providerName."],
  ],
  "Motor": [
    ["RoutineGenerator.generate", "Gym o plantilla del deporte. Siempre siete días."],
    ["seedOf", "id ⊕ equipo ⊕ nivel ⊕ objetivo. Misma entrada, misma rutina."],
    ["gymPlan / pickExercises / toExercise", "Split, filtro de máquinas, series, reps y descanso."],
    ["GymExerciseCatalog.available", "Deja ejercicios cuyo equipo está marcado. Si no hay, usa los de peso corporal."],
    ["SportSessionTemplates.sessions", "running, yoga, pilates, cycling, swimming, crossfit, boxing."],
    ["MealPlanGenerator.optionsFor", "Cinco recetas por comida, puntuadas y rotadas por día."],
    ["LocalCoach.answer", "Intención por palabras clave, respuesta con datos del perfil."],
    ["RemoteAiRepository.requestCompletion", "POST /chat/completions. Fallback a LocalAiRepository."],
  ],
  "Datos": [
    ["FormaDatabase + DAOs", "observe, upsert, clear por perfil, entrenamiento, comida, comunidad, chat y artículos."],
    ["FormaPreferences", "saveUser, clearUser, setRestSeconds, setRemindersEnabled."],
    ["ImageStore.persist", "Copia la foto elegida al almacenamiento interno."],
    ["CloudSyncManager", "pullIfNeeded al iniciar sesión y push de cada cambio."],
    ["FirebaseCloudStore", "upsert de perfil, logs, sesiones, comidas, chat, artículos, posts y archivos."],
    ["NoOpCloudStore", "Contrato remoto apagado. La app sigue en Room."],
    ["UserProfile.dailyCalories", "Mifflin–St Jeor × actividad + calorieShift, redondeado a 10 kcal."],
    ["UserProfile.proteinTargetG", "2.0, 2.2 o 1.7 g por kg según el objetivo."],
  ],
};

const tabBar = document.getElementById("layer-tabs");
const layerBody = document.getElementById("layer-body");
function renderLayer(name) {
  [...tabBar.children].forEach((button) => button.classList.toggle("on", button.textContent === name));
  layerBody.innerHTML = `<table><tbody>${layers[name].map(([fn, desc]) =>
    `<tr><td style="width:34%"><code>${fn}</code></td><td>${desc}</td></tr>`
  ).join("")}</tbody></table>`;
}
Object.keys(layers).forEach((name) => {
  const button = document.createElement("button");
  button.type = "button";
  button.textContent = name;
  button.onclick = () => renderLayer(name);
  tabBar.appendChild(button);
});
renderLayer("Repositorios");

const initial = Number((location.hash || "").replace("#", ""));
if (initial >= 1 && initial <= slides.length) show(initial - 1);
else show(0);
