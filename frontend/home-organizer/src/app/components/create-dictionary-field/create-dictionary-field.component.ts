import {Component, OnInit} from '@angular/core';
import {FieldType} from "../../models/field-type";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {DictionaryField} from "../../models/dictionary-field";

@Component({
  selector: 'app-create-field',
  templateUrl: './create-dictionary-field.component.html'
})
export class CreateDictionaryFieldComponent {
  types = Object.entries(FieldType).map(([key, value]) => ({id: key, value: value}))

  constructor() {
  }

  form = new FormGroup({
    name: new FormControl<string>('', Validators.required),
    type: new FormControl<string>('', Validators.required),
    required: new FormControl<boolean>(false),
    unique: new FormControl<boolean>(false),
    defaultValue: new FormControl<string>('')
  })

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

  get(): DictionaryField {
    return <DictionaryField>this.form.value
  }
}
