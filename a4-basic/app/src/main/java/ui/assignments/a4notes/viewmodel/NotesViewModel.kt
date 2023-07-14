package ui.assignments.a4notes.viewmodel

import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import ui.assignments.a4notes.model.Model

class NotesViewModel : ViewModel() {

    /**
     *  The representation of a [Model.ModelNote] in the ViewModel. Only [VMNote]s are exposed to the View.
     */
    data class VMNote(val id: Int, var title: String, var content: String, var important : Boolean = false, var archived : Boolean = false)  {
        constructor(note : Model.ModelNote) : this(note.id, note.title, note.content, note.important, note.archived)
    }

    // model
    private val model = Model()

    // list of all currently visible / displayed notes
    private var notes = MutableLiveData<MutableList<MutableLiveData<VMNote>>>(mutableListOf())

    //for the edit screen
    private val curEditingNoteId = MutableLiveData(0)

    // UI state indicating if archived notes should be displayed
    private val viewArchived = MutableLiveData(false)

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel>
            create(modelClass: Class<T>, extras: CreationExtras): T {
                return NotesViewModel() as T
            }
        }
    }

    init {
        // noteChangeCallback responds to all changes *within* a ModelNote:
        //   onPropertyChanged received notifications from ModelNote if its state has changed, and updates the
        //   corresponding MVNote accordingly. The VMNote is wrapped in MutableLiveData and exposed as LiveData to the
        //   View.
        val noteChangeCallback = object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(modelNote: Observable?, propertyId: Int) {
                Log.v("In noteChangeCallback", "viewArchived:${viewArchived.value}") //for testing
                notes.value?.find { it.value?.id == propertyId }?.apply {// find MVNote in notes
                    modelNote as Model.ModelNote
                    this.value = VMNote(modelNote.id, modelNote.title, modelNote.content, modelNote.important, modelNote.archived)
                    // DO NOT NEED THIS IN MY IMPLEMENTATION
                    //if (modelNote.archived && viewArchived.value == false) { // if note is archived and archived notes are not showing, remove from notes
                    //    notes.value = notes.value.apply {
                    //        this?.removeIf { note -> note.value?.id == value?.id }
                    //    }
                    //} else { // if not, apply changes from ModelNote to MVNote
                        notes.value = notes.value.apply {
                            this?.sortWith { a, b -> model.compareNotes(a?.value!!.id, b?.value!!.id) }
                    //   }
                    }
                }
            }
        }

        // addOnListChangedCallback responds to all changes of the list of ModelNotes:
        //   onItemRangeInserted is called if a ModelNote is successfully added to the Model
        //   onItemRangeRemoved is called if a ModelNote is successfully removed from the Model
        model.notes.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<Model.ModelNote>>() {
            override fun onChanged(sender: ObservableArrayList<Model.ModelNote>?) {  }
            override fun onItemRangeChanged(sender: ObservableArrayList<Model.ModelNote>?, positionStart: Int, itemCount: Int) {  }
            override fun onItemRangeInserted(sender: ObservableArrayList<Model.ModelNote>?, positionStart: Int, itemCount: Int) {
                val addedNote = sender?.get(positionStart) // get new ModelNote
                addedNote?.addOnPropertyChangedCallback(noteChangeCallback) // add listener to ModelNote
                Log.v("In addOnListChangedCallback", "viewArchived:${viewArchived.value}") //for testing
                //if (addedNote?.archived?.not() == true) { //
                    //Log.v("In addOnListChangedCallback past if", "viewArchived:${viewArchived.value}") //for testing
                    notes.value = notes.value.apply {
                        this?.add(MutableLiveData(addedNote?.let { VMNote(it) }))
                        this?.sortWith { p0, p1 -> model.compareNotes(p0?.value!!.id, p1?.value!!.id) }
                    }
                //} //removed this conditional archive adding to notes, include all notes
            }
            override fun onItemRangeMoved(sender: ObservableArrayList<Model.ModelNote>?, fromPosition: Int, toPosition: Int, itemCount: Int) {  }
            override fun onItemRangeRemoved(sender: ObservableArrayList<Model.ModelNote>?, positionStart: Int, itemCount: Int) {
                notes.value = notes.value!!.apply {
                    removeIf { mvnote -> (sender?.find { modelnote -> modelnote.id == mvnote.value!!.id }) == null }
                }
            }
        })

        model.addSomeNotes()
    }


    /**
     * Returns a read-only version of [notes], which stores read-only observables of [VMNote]. Observe to receive notifications about changes to the list.
     * @see notes
     */
    fun getNotes() : LiveData<MutableList<LiveData<VMNote>>> {
        //Log.v("In getNotes 1", "")

        return notes as LiveData<MutableList<LiveData<VMNote>>>
    }

    // The following methods are missing:
    // * Functions to get / set the value of viewArchived.
    fun getViewArchived() : Boolean? {
        return viewArchived.value
    }

    fun setViewArchived(newValue : Boolean) {
        Log.v("In setViewArchived", "newValue:$newValue")
        viewArchived.value = newValue

        model.updateNoteTitle(0, "") //force refresh by changing livedata (note zero (dev note))
    }
    // * Functions to forward requests from the View to the Model.
    fun addNote(title: String, content: String, important: Boolean = false) {
        model.addNote(title, content, important)
    }

    fun removeNote(id: Int) {
        model.removeNote(id)
    }

    fun updateNoteTitle(id: Int, title: String) {
        model.updateNoteTitle(id, title)
    }

    fun updateNoteContent(id: Int, content: String) {
        model.updateNoteContent(id, content)
    }

    fun updateNoteArchived(id: Int, archived: Boolean) {
        model.updateNoteArchived(id, archived)
    }

    fun updateNoteImportant(id: Int, important: Boolean) {
        model.updateNoteImportant(id, important)
    }

    //editing screen functions
    fun getCurEditingScreenId() : LiveData<Int> {
        Log.v("here4", "${curEditingNoteId.value}")
        return curEditingNoteId
    }

    fun setCurEditingScreenId(id : Int){
        Log.v("here3", "${curEditingNoteId.value}")
        curEditingNoteId.value = id
        Log.v("here3", "${curEditingNoteId.value}")
    }

}