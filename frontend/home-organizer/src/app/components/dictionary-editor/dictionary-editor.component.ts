import {Component, EventEmitter, Input, OnInit, Output, ViewChild, ViewRef} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {DictionaryService} from "../../services/dictionary.service";
import {Dictionary} from "../../models/dictionary";
import {CreateDictionaryFieldComponent} from "../dictionary-field-editor/dictionary-field-editor.component";
import {RefDirective} from "../../directives/ref.directive";
import {BehaviorSubject, concatMap, tap} from "rxjs";
import {DictionaryField} from "../../models/dictionary-field";

@Component({
  selector: 'app-dictionary-editor',
  templateUrl: './dictionary-editor.component.html'
})
export class DictionaryEditorComponent implements OnInit {
  @ViewChild(RefDirective, {static: true}) refDir: RefDirective
  @Input() visible$: BehaviorSubject<boolean>
  @Input() dictionary: Dictionary
  @Output() itemChanged = new EventEmitter()

  form = this.builder.group({
    name: ['', {
      validators: [
        Validators.required,
        Validators.minLength(3)
      ]
    }],
    description: [''],
    fields: this.builder.array([])
  })

  constructor(
    private dictionaryService: DictionaryService,
    private builder: FormBuilder
  ) {
  }

  ngOnInit(): void {
    this.setDicValues()
  }

  private setDicValues() {
    if (this.dictionary) {
      this.form.controls.name.setValue(this.dictionary.name)
      this.form.controls.description.setValue(this.dictionary.description ? this.dictionary.description : '')
      this.refDir.containerRef.clear()
      this.dictionary.fields.forEach(fld => {
        this.addExistedField(fld)
      })
    }
  }

  get name() {
    return this.form.controls.name as FormControl
  }

  get fields() {
    return this.form.controls.fields as FormArray
  }

  create() {
    this.dictionaryService.create(<Dictionary>this.form.value)
      .pipe(
        tap(() => this.itemChanged.emit())
      )
      .subscribe(() => {
        this.close()
        this.clean()
      })
  }

  addField() {
    const field = this.refDir.containerRef.createComponent(CreateDictionaryFieldComponent)
    field.instance.component = this
    field.instance.form.controls.position.setValue(this.fields.length)
    this.fields.push(field.instance.form)
  }

  addExistedField(fld: DictionaryField) {
    const field = this.refDir.containerRef.createComponent(CreateDictionaryFieldComponent)
    field.instance.component = this
    field.instance.field = fld
    this.fields.push(field.instance.form)
  }

  deleteField(form: FormGroup) {
    let index = this.fields.controls.indexOf(form)
    this.refDir.containerRef.remove(index)
    this.fields.removeAt(index)
  }


  clean() {
    this.form.reset()
    this.form.controls.fields.clear()
    this.refDir.containerRef.clear()
  }

  close() {
    this.visible$.next(false)
    this.setDicValues()
  }

  update() {
      this.dictionaryService.update(this.dictionary.id, <Dictionary>this.form.value)
        .pipe(
          tap(() => this.itemChanged.emit())
        ).subscribe(() => {
        this.close()
      })
  }

  up(form: FormGroup) {
    let index = this.fields.controls.indexOf(form)
    if (index > 0) {
      let upperIndex = index - 1

      let element = this.fields.controls.at(index) as FormControl
      let upperElement = this.fields.controls.at(upperIndex) as FormControl
      element.value.position = upperIndex
      upperElement.value.position = index

      this.fields.removeAt(index)
      this.fields.insert(upperIndex, element)

      let view = this.refDir.containerRef.get(index) as ViewRef
      this.refDir.containerRef.move(view, upperIndex)
    }
  }

  down(form: FormGroup) {
    let index = this.fields.controls.indexOf(form)
    if (index < this.fields.length - 1) {
      let lowerIndex = index + 1

      let element = this.fields.controls.at(index) as FormControl
      let upperElement = this.fields.controls.at(lowerIndex) as FormControl
      element.value.position = lowerIndex
      upperElement.value.position = index

      this.fields.removeAt(index)
      this.fields.insert(lowerIndex, element)

      let view = this.refDir.containerRef.get(index) as ViewRef
      this.refDir.containerRef.move(view, lowerIndex)
    }
  }
}
