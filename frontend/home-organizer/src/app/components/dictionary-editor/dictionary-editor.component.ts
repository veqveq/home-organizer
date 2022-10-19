import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormControl, Validators} from "@angular/forms";
import {DictionaryService} from "../../services/dictionary.service";
import {Dictionary} from "../../models/dictionary";
import {CreateDictionaryFieldComponent} from "../dictionary-field-editor/dictionary-field-editor.component";
import {RefDirective} from "../../directives/ref.directive";
import {BehaviorSubject, concatMap} from "rxjs";
import {DictionaryField} from "../../models/dictionary-field";

@Component({
  selector: 'app-dictionary-editor',
  templateUrl: './dictionary-editor.component.html'
})
export class DictionaryEditorComponent implements OnInit {
  ngOnInit(): void {
    if (this.dictionary) {
      this.form.controls.name.setValue(this.dictionary.name)
      this.form.controls.description.setValue(this.dictionary.description ? this.dictionary.description : '')
      // this.form.controls.fields.setValue(this.dictionary.fields)
      this.dictionary.fields.forEach(fld => {
        this.addExistedField(fld)
        // const field = this.refDir.containerRef.createComponent(CreateDictionaryFieldComponent)
        console.log(fld)
        // field.instance.field = fld
      })
    }
  }

  @ViewChild(RefDirective, {static: true}) refDir: RefDirective
  @Input() visible$: BehaviorSubject<boolean>
  @Input() dictionary: Dictionary

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

  get name() {
    return this.form.controls.name as FormControl
  }

  get fields() {
    return this.form.controls.fields as FormArray
  }

  constructor(
    private dictionaryService: DictionaryService,
    private builder: FormBuilder
  ) {
  }

  create() {
    this.dictionaryService.create(<Dictionary>this.form.value)
      .pipe(concatMap(() => this.dictionaryService.getAll()))
      .subscribe(() => {
        this.close()
        this.clean()
      })
  }

  addField() {
    const field = this.refDir.containerRef.createComponent(CreateDictionaryFieldComponent)
    field.instance.component = this
    field.instance.index = this.fields.length
    this.fields.push(field.instance.form)
  }

  addExistedField(fld: DictionaryField) {
    const field = this.refDir.containerRef.createComponent(CreateDictionaryFieldComponent)
    field.instance.component = this
    field.instance.index = this.fields.length
    field.instance.field = fld
    this.fields.push(field.instance.form)
  }

  deleteField(index: number) {
    this.refDir.containerRef.remove(index)
    this.fields.removeAt(index)
  }


  clean() {
    this.form.reset()
    this.refDir.containerRef.clear()
  }

  close() {
    this.visible$.next(false)
  }

  update() {
    if (this.dictionary.id) {
      this.dictionaryService.update(this.dictionary.id, <Dictionary>this.form.value)
        .pipe(concatMap(() => this.dictionaryService.getAll()))
        .subscribe(() => {
          this.close()
        })
    }
  }
}
