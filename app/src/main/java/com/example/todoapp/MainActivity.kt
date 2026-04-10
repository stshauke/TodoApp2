package com.example.todoapp

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.Task
import com.example.todoapp.ui.TaskAdapter
import com.example.todoapp.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

/**
 * MainActivity : chef d'orchestre de l'application
 * Rôle dans MVVM : observer → afficher → déléguer
 * Ne touche JAMAIS la base de données directement
 *
 * Phase 4 — nouvelles fonctionnalités :
 * - Modification d'une tâche (appui long → dialog pré-remplie)
 * - Filtrage par onglets : Toutes / Actives / Terminées
 */
class MainActivity : AppCompatActivity() {

    /**
     * by viewModels() : crée et conserve le ViewModel
     * Survit aux rotations d'écran — pas de perte de données
     */
    private val viewModel: TaskViewModel by viewModels()

    // Adapter initialisé dans onCreate — lateinit évite le nullable
    private lateinit var adapter: TaskAdapter

    // Filtre actif : "all" par défaut au démarrage
    private var currentFilter = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Toolbar ---
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        // Définit la Toolbar comme ActionBar → active le titre, menu, bouton retour
        setSupportActionBar(toolbar)

        /**
         * Adapter initialisé avec 3 lambdas :
         * onToggle → coche/décoche via ViewModel
         * onDelete → supprime via ViewModel
         * onEdit   → ouvre la dialog de modification (appui long)
         */
        adapter = TaskAdapter(
            onToggle = { task -> viewModel.toggleDone(task) },
            onDelete = { task -> viewModel.deleteTask(task) },
            onEdit   = { task -> showEditTaskDialog(task) }  // ← 3ème paramètre
        )

        // --- RecyclerView ---
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // LinearLayoutManager : liste verticale, un item en dessous de l'autre
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // --- TextView affiché quand la liste est vide ---
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)

        // Lance l'observation avec le filtre par défaut "all"
        observeWithFilter(tvEmpty)

        // --- TabLayout : onglets de filtre ---
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        /**
         * OnTabSelectedListener : réagit aux changements d'onglet
         * position 0 = Toutes, 1 = Actives, 2 = Terminées
         */
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Met à jour le filtre selon l'onglet cliqué
                currentFilter = when (tab?.position) {
                    1    -> "active"   // onglet Actives
                    2    -> "done"     // onglet Terminées
                    else -> "all"      // onglet Toutes (défaut)
                }
                // Relance l'observation avec le nouveau filtre
                observeWithFilter(tvEmpty)
            }
            // Méthodes obligatoires non utilisées ici
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // --- FAB : bouton "+" pour ajouter ---
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { showAddTaskDialog() }
    }

    /**
     * observeWithFilter : observe le LiveData filtré et met à jour l'UI
     *
     * removeObservers() : supprime l'ancien observer avant d'en créer un nouveau
     * Évite les doublons qui causeraient des mises à jour multiples
     */
    private fun observeWithFilter(tvEmpty: TextView) {
        // Supprime les anciens observers pour éviter les doublons
        viewModel.allTasks.removeObservers(this)

        // Observe le LiveData filtré selon currentFilter
        viewModel.getFilteredTasks(currentFilter).observe(this) { tasks ->
            // DiffUtil calcule les différences et anime uniquement les changements
            adapter.submitList(tasks)
            // Affiche "liste vide" si aucune tâche, sinon cache le message
            tvEmpty.visibility = if (tasks.isEmpty()) TextView.VISIBLE else TextView.GONE
        }
    }

    /**
     * showAddTaskDialog : dialog pour créer une nouvelle tâche
     * Appelé par le FAB
     */
    private fun showAddTaskDialog() {
        val input = EditText(this).apply {
            hint = "Ex: Acheter du pain"
            setPadding(48, 32, 48, 16)
        }
        AlertDialog.Builder(this)
            .setTitle("Nouvelle tâche")
            .setView(input)
            .setPositiveButton("Ajouter") { _, _ ->
                // trim() supprime les espaces en début/fin
                val title = input.text.toString().trim()
                // N'ajoute pas si le titre est vide
                if (title.isNotEmpty()) viewModel.addTask(title)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    /**
     * showEditTaskDialog : dialog pour modifier une tâche existante
     * Appelé par un appui long sur une carte dans le RecyclerView
     *
     * Différence avec showAddTaskDialog :
     * - setText() pré-remplit le champ avec le titre actuel
     * - setSelection() place le curseur à la fin pour faciliter l'édition
     * - Appelle editTask() au lieu de addTask()
     *
     * @param task : la tâche à modifier, reçue depuis TaskAdapter
     */
    private fun showEditTaskDialog(task: Task) {
        val input = EditText(this).apply {
            // Pré-remplit avec le titre actuel de la tâche
            setText(task.title)
            // Curseur à la fin du texte existant
            setSelection(task.title.length)
            setPadding(48, 32, 48, 16)
        }
        AlertDialog.Builder(this)
            .setTitle("Modifier la tâche")
            .setView(input)
            .setPositiveButton("Enregistrer") { _, _ ->
                val newTitle = input.text.toString().trim()
                // N'enregistre pas si le nouveau titre est vide
                if (newTitle.isNotEmpty()) {
                    // Passe la tâche originale + le nouveau titre au ViewModel
                    viewModel.editTask(task, newTitle)
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
}