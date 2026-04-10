package com.example.todoapp.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.Task

/**
 * TaskAdapter : pont entre la liste de tâches (données) et le RecyclerView (écran)
 *
 * @param onToggle : appelé quand l'utilisateur coche/décoche une tâche
 * @param onDelete : appelé quand l'utilisateur clique sur la corbeille
 * @param onEdit   : appelé quand l'utilisateur fait un appui LONG sur une carte
 *                   → ouvre la dialog de modification dans MainActivity
 */
class TaskAdapter(
    private val onToggle: (Task) -> Unit,
    private val onDelete: (Task) -> Unit,
    private val onEdit:   (Task) -> Unit   // ← NOUVEAU : lambda pour modifier
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {

    /**
     * TaskViewHolder : contient les références aux vues de item_task.xml
     * Créé une seule fois par cellule visible → réutilisé pendant le scroll
     */
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkDone: CheckBox    = itemView.findViewById(R.id.checkDone)
        val tvTitle:   TextView    = itemView.findViewById(R.id.tvTitle)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    /**
     * onCreateViewHolder : gonfle item_task.xml en objet View
     * Appelé uniquement quand une nouvelle cellule est nécessaire
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    /**
     * onBindViewHolder : remplit un ViewHolder existant avec les données d'une tâche
     * Appelé à chaque scroll pour réutiliser les cellules
     */
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)

        // Affiche le titre
        holder.tvTitle.text = task.title

        // Retire l'ancien listener avant de cocher pour éviter les
        // déclenchements indésirables lors du recyclage des vues
        holder.checkDone.setOnCheckedChangeListener(null)
        holder.checkDone.isChecked = task.isDone

        // Texte barré si la tâche est terminée, normal sinon
        if (task.isDone) {
            holder.tvTitle.paintFlags =
                holder.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvTitle.paintFlags =
                holder.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Appui sur CheckBox → inverse isDone via ViewModel
        holder.checkDone.setOnCheckedChangeListener { _, _ ->
            onToggle(task)
        }

        // Appui sur corbeille → supprime la tâche via ViewModel
        holder.btnDelete.setOnClickListener {
            onDelete(task)
        }

        // ← NOUVEAU : appui long sur la carte → ouvre la dialog de modification
        // setOnLongClickListener retourne true pour indiquer que
        // l'événement est consommé ici et ne doit pas se propager
        holder.itemView.setOnLongClickListener {
            onEdit(task)
            true
        }
    }

    /**
     * DiffCallback : compare deux listes pour animer uniquement les changements
     * areItemsTheSame → même tâche ? (compare les id)
     * areContentsTheSame → tâche modifiée ? (compare tous les champs via data class)
     */
    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}