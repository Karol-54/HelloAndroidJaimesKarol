package com.jaimes.helloandroid.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jaimes.helloandroid.databinding.FragmentTaskDetailBinding
import com.jaimes.helloandroid.viewmodel.TaskViewModel

class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskId = args.taskId

        if (taskId != -1) {
            binding.tvDetailTitle.text = "Editar Tarea"
            val task = viewModel.tasks.value?.find { it.id == taskId }
            task?.let {
                binding.etTitle.setText(it.title)
                binding.etDescription.setText(it.description)
                binding.switchReminder.isChecked = it.hasReminder
            }
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            val hasReminder = binding.switchReminder.isChecked

            if (title.isEmpty()) {
                binding.tilTitle.error = "El título es obligatorio"
                return@setOnClickListener
            }
            binding.tilTitle.error = null

            if (taskId == -1) {
                viewModel.addTask(title, desc, hasReminder)
            } else {
                val updated = viewModel.tasks.value
                    ?.find { it.id == taskId }
                    ?.copy(title = title, description = desc, hasReminder = hasReminder)
                updated?.let { viewModel.updateTask(it) }
            }

            if (hasReminder) {
                scheduleReminder(title)
                Toast.makeText(requireContext(),
                    "Recordatorio en 30 segundos", Toast.LENGTH_SHORT).show()
            }

            findNavController().navigateUp()
        }
    }

    private fun scheduleReminder(taskTitle: String) {
        val intent = Intent(requireContext(), TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TITLE, taskTitle)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            taskTitle.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + 30_000L
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}