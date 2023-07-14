package ui.assignments.a4notes.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel


class AddScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_add_screen, container, false)

        val model: NotesViewModel by activityViewModels { NotesViewModel.Factory }

        val importantSwitch = root.findViewById<Switch>(R.id.ImportantSwitch)
        val editTitle = root.findViewById<EditText>(R.id.EditTitle)
        val editContent = root.findViewById<EditText>(R.id.EditContent)
        val createButton = root.findViewById<Button>(R.id.CreateButton)

        createButton.setOnClickListener{
            model.addNote(editTitle.text.toString(), editContent.text.toString(), importantSwitch.isChecked)
            findNavController().navigate(R.id.action_addScreenFragment_to_MainFragment)
        }

        val conLay = root.findViewById<ConstraintLayout>(R.id.ConstraintLayout)
        if (model.getIsDarkMode()) {
            conLay.setBackgroundColor(Color.parseColor("#313639")) //off black
        } else {
            conLay.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        return root
    }
}