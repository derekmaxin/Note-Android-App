package ui.assignments.a4notes.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_main, container, false)

        //VERY IMPORTANT USE activityViewModels INSTEAD OF viewModels TO SYNC FRAGMENTS TO VIEWMODEL DATA
        val model : NotesViewModel by activityViewModels { NotesViewModel.Factory }

        val listOfNotes = root.findViewById<LinearLayout>(R.id.ListOfNotes)

        model.getNotes().observe(viewLifecycleOwner) { it ->
            Log.i("MainActivity", it?.fold("Visible Note IDs:") { acc, cur -> "$acc ${cur.value?.id}" } ?: "[ERROR]")

            //remove children from prev render
            listOfNotes.removeAllViews()

            it.forEachIndexed { index, element ->
                if ((!(model.getViewArchived() == false && element.value?.archived == true))
                    && !(element.value?.id == 0)) { //don't display archived when turned oof or dev note
                    val noteLayout = LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(20, 20, 0, 0)
                        //works to change background of entire note
                        if (element.value?.archived == true) {
                            setBackgroundColor(Color.parseColor("#D3D3D3")) //light grey
                        }
                        if (element.value?.important == true) {
                            setBackgroundColor(Color.parseColor("#FFFF00")) //yellow
                        }
                    }
                    val noteTextSection = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                    }
                    val noteButtonSection = LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL
                    }
                    val noteTitle = TextView(context).apply {
                        text = element.value?.title
                        setTypeface(null, Typeface.BOLD)
                        minWidth = 730
                        maxWidth = 730
                        maxLines = 1
                    }
                    val noteText = TextView(context).apply {
                        text = element.value?.content
                        minWidth = 730
                        maxWidth = 730
                        maxLines = 2
                    }
                    val dividerV = TextView(context).apply {
                        text = ""
                        maxHeight = 20
                        minHeight = 20
                    }
                    noteTextSection.addView(noteTitle)
                    noteTextSection.addView(noteText)
                    noteTextSection.addView(dividerV)

                    val archiveButton = Button(context).apply {
                        //changed from Button to TextView to obey my width changing
                        text = "A"
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        textSize = 10.0F
                        setTextColor(Color.parseColor("#FFFFFF")) //white
                        setBackgroundColor(Color.parseColor("#F699CD")) //pink
                        setLayoutParams(
                            ViewGroup.LayoutParams(
                                LinearLayout.LayoutParams(
                                    125,
                                    100
                                )
                            )
                        ) //import LayoutParams from ViewGroup
                        setOnClickListener {
                            // logic for archive button
                            element.value?.let { it1 -> model.updateNoteArchived(it1.id, !(element.value?.archived)!!) }
                        }
                    }
                    val deleteButton = Button(context).apply {
                        //changed from Button to TextView to obey my width changing
                        text = "D"
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        textSize = 10.0F
                        setTextColor(Color.parseColor("#FFFFFF")) //white
                        setBackgroundColor(Color.parseColor("#F699CD")) //pink
                        setLayoutParams(ViewGroup.LayoutParams(LinearLayout.LayoutParams(125, 100)))
                        setOnClickListener {
                            // logic for delete button
                            element.value?.let { it1 -> model.removeNote(it1.id) }
                        }
                    }
                    val dividerH = TextView(context).apply {
                        text = ""
                        minWidth = 20
                        maxWidth = 20
                        maxHeight = 100
                    }
                    val dividerH2 = TextView(context).apply {
                        text = ""
                        minWidth = 20
                        maxWidth = 20
                        maxHeight = 100
                    }
                    noteButtonSection.addView(dividerH)
                    noteButtonSection.addView(archiveButton)
                    noteButtonSection.addView(dividerH2)
                    noteButtonSection.addView(deleteButton)

                    noteLayout.addView(noteTextSection)
                    noteLayout.addView(noteButtonSection)


                    listOfNotes.addView(noteLayout)
                    //Log.v("rendered notes", "rendered!")

                    //navigation WORKS!!
                    noteLayout.setOnClickListener {
                        //let the model know which note we are editing (new fragment will reference this)
                        Log.v("here2", "${element.value?.id}")
                        element.value?.id?.let { it1 -> model.setCurEditingScreenId(it1) }

                        //navigate to editing screen fragment
                        findNavController().navigate(R.id.action_MainFragment_to_EditScreenFragment)
                    }
                }

            }


        }

        val showArchivedSwitch = root.findViewById<Switch>(R.id.showArchivedSwitch)
        showArchivedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            model.setViewArchived(isChecked)
        }

        val newNoteButton = root.findViewById<Button>(R.id.NewNoteButton)
        newNoteButton.setOnClickListener{
            findNavController().navigate(R.id.action_MainFragment_to_addScreenFragment)
        }


        return root

    }

}