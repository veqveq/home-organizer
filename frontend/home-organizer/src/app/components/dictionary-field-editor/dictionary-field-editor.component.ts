import {Component, Input, OnInit} from '@angular/core';
import {FieldType} from "../../models/field-type";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {DictionaryField} from "../../models/dictionary-field";
import {DictionaryEditorComponent} from "../dictionary-editor/dictionary-editor.component";

@Component({
  selector: 'app-create-field',
  templateUrl: './dictionary-field-editor.component.html'
})
export class CreateDictionaryFieldComponent implements OnInit {

  @Input('field') field?: DictionaryField
  types = Object.entries(FieldType).map(([key, value]) => ({id: key, value: value}))
  component: DictionaryEditorComponent
  form = new FormGroup({
    id: new FormControl<string>(''),
    position: new FormControl<number>(-1),
    name: new FormControl<string>('', Validators.required),
    type: new FormControl<string>('', Validators.required),
    required: new FormControl<boolean>(false),
    unique: new FormControl<boolean>(false),
    defaultValue: new FormControl<string>('')
  })

  constructor() {
  }

  ngOnInit(): void {
    if (this.field) {
      this.form.controls.id.setValue(this.field.id ? this.field.id : '')
      this.form.controls.position.setValue(this.field.position)
      this.form.controls.name.setValue(this.field.name)
      this.form.controls.type.setValue(this.field.type)
      this.form.controls.required.setValue(this.field.required ? this.field.required : false)
      this.form.controls.unique.setValue(this.field?.unique ? this.field.unique : false)
      this.form.controls.defaultValue.setValue(this.field?.defaultValue ? this.field.defaultValue : '')
    }
  }

  get name() {
    return this.form.controls.name as FormControl
  }

  get type() {
    return this.form.controls.type as FormControl
  }

  get required() {
    return this.form.controls.required as FormControl
  }

  get defaultValue() {
    return this.form.controls.defaultValue as FormControl
  }

  get position() {
    return this.form.controls.position.value as number
  }

  get(): DictionaryField {
    return <DictionaryField>this.form.value
  }

  deleteComponent() {
    this.component.deleteField(this.form)
  }

  left() {
    this.component.up(this.form)
  }

  right() {
    this.component.down(this.form)
  }
}
