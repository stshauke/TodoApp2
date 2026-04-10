package com.example.todoapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests instrumentés Espresso — s'exécutent sur l'émulateur
 *
 * Espresso pilote l'UI comme un vrai utilisateur :
 * - perform(click())       → simule un tap
 * - perform(typeText())    → simule une saisie clavier
 * - check(matches(...))    → vérifie l'état d'une vue
 *
 * Chaque test repart de zéro grâce à ActivityScenarioRule
 * qui redémarre MainActivity avant chaque test
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    /**
     * ActivityScenarioRule : démarre MainActivity avant chaque test
     * et la ferme proprement après — garantit un état propre
     */
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Test 1 : les éléments principaux sont visibles au démarrage
     */
    @Test
    fun mainScreen_elementsAreDisplayed() {
        // RecyclerView visible
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))

        // Bouton + visible
        onView(withId(R.id.fab))
            .check(matches(isDisplayed()))

        // Onglets visibles
        onView(withId(R.id.tabLayout))
            .check(matches(isDisplayed()))
    }

    /**
     * Test 2 : ajouter une tâche → elle apparaît dans la liste
     *
     * Simule exactement ce que ferait un utilisateur :
     * 1. Tap sur +
     * 2. Saisie du titre
     * 3. Tap sur "Ajouter"
     * 4. Vérification visuelle
     */
    @Test
    fun addTask_appearsInList() {
        // Tape sur le bouton +
        onView(withId(R.id.fab))
            .perform(click())

        // Saisit le titre dans le champ (trouvé par son hint)
        onView(withHint("Ex: Acheter du pain"))
            .perform(
                typeText("Boire de l'eau"),
                closeSoftKeyboard()
            )

        // Tape sur "Ajouter"
        onView(withText("Ajouter"))
            .perform(click())

        // Vérifie que la tâche apparaît dans la liste
        onView(withText("Boire de l'eau"))
            .check(matches(isDisplayed()))
    }

    /**
     * Test 3 : la toolbar affiche le bon titre
     */
    @Test
    fun toolbar_showsCorrectTitle() {
        onView(withText("Mes tâches"))
            .check(matches(isDisplayed()))
    }

    /**
     * Test 4 : titre vide → aucune tâche ajoutée
     *
     * Vérifie que la validation fonctionne :
     * si le titre est vide, la tâche n'est pas créée
     * et le message "liste vide" reste affiché
     */
    @Test
    fun addTask_emptyTitle_doesNotAdd() {
        // Ouvre la dialog sans saisir de titre
        onView(withId(R.id.fab)).perform(click())

        // Tape directement "Ajouter" sans saisir de texte
        onView(withText("Ajouter")).perform(click())

        // Le message "liste vide" doit rester visible
        onView(withId(R.id.tvEmpty))
            .check(matches(isDisplayed()))
    }
}