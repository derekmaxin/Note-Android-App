package ui.assignments.a4notes.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ui.assignments.a4notes.R
import ui.assignments.a4notes.viewmodel.NotesViewModel


class EditScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_edit_screen, container, false)

        val model : NotesViewModel by activityViewModels { NotesViewModel.Factory }

        val notes = model.getNotes()
        val noteId = model.getCurEditingScreenId().value

        val importantSwitch = root.findViewById<Switch>(R.id.ImportantSwitch)
        val archivedSwitch = root.findViewById<Switch>(R.id.ArchivedSwitch)
        val editTitle = root.findViewById<EditText>(R.id.EditTitle)
        val editContent = root.findViewById<EditText>(R.id.EditContent)


        notes.value?.forEachIndexed { index, element ->
            if (noteId == element.value?.id) {
                //Log.v("here", "yis")
                //the element is now the note we are editing

                //setting our current state
                Log.v("here", "${model.getCurEditingScreenId().value}")
                Log.v("here", "${importantSwitch.isChecked}")
                Log.v("here", "${element.value!!.important}")
                importantSwitch.isChecked = element.value!!.important
                archivedSwitch.isChecked = element.value!!.archived
                editTitle.setText(element.value!!.title)
                editContent.setText(element.value!!.content)

                //functionality
                importantSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (noteId != null) {
                        model.updateNoteImportant(noteId, isChecked)
                        if (isChecked) archivedSwitch.isChecked = false //will trigger importantSwitch listener
                    }
                }
                archivedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (noteId != null) {
                        model.updateNoteArchived(noteId, isChecked)
                        if (isChecked) importantSwitch.isChecked = false //will trigger archivedSwitch listener
                    }
                }
                editTitle.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        if (noteId != null) {
                            model.updateNoteTitle(noteId, editTitle.text.toString())
                        }
                    }
                    override fun afterTextChanged(s: Editable?) {
                    }
                })
                editContent.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        if (noteId != null) {
                            model.updateNoteContent(noteId, editContent.text.toString())
                        }
                    }
                    override fun afterTextChanged(s: Editable?) {
                    }
                })




            }
        }




        return root

    }

}