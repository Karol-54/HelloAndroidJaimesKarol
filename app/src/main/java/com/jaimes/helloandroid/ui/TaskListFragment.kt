package com.jaimes.helloandroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaimes.helloandroid.databinding.FragmentTaskListBinding
import com.jaimes.helloandroid.viewmodel.TaskViewModel

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()

    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TaskAdapter { task ->
            val action = TaskListFragmentDirections
                .actionTaskListFragmentToTaskDetailFragment(taskId = task.id)
            findNavController().navigate(action)
        }

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = adapter

        viewModel.tasks.observe(viewLifecycleOwner) { taskList ->
            adapter.submitList(taskList)
        }

        binding.fabAddTask.setOnClickListener {
            val action = TaskListFragmentDirections
                .actionTaskListFragmentToTaskDetailFragment(taskId = -1)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}