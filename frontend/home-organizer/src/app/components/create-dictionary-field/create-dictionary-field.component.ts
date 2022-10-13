import {Component, Input, OnInit} from '@angular/core';
import {FieldType} from "../../models/field-type";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {DictionaryField} from "../../models/dictionary-field";
import {CreateDictionaryComponent} from "../create-dictionary/create-dictionary.component";

@Component({
  selector: 'app-create-field',
  templateUrl: './create-dictionary-field.component.html'
})
export class CreateDictionaryFieldComponent {
  @Input('field') field?: DictionaryField
  types = Object.entries(FieldType).map(([key, value]) => ({id: key, value: value}))
  index: number
  component: CreateDictionaryComponent
  form = new FormGroup({
    name: new FormControl<string>(this.field?.name ? this.field.name : '', Validators.required),
    type: new FormControl<string>(this.field?.type ? this.field.type : '', Validators.required),
    required: new FormControl<boolean>(this.field?.required ? this.field.required : false),
    unique: new FormControl<boolean>(this.field?.unique ? this.field.unique : false),
    defaultValue: new FormControl<string>(this.field?.defaultValue ? this.field.defaultValue : '')
  })

  constructor() {
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

  get(): DictionaryField {
    return <DictionaryField>this.form.value
  }

  deleteComponent() {
    this.component.deleteField(this.index)
  }
}
