import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DictionaryRegisterComponent} from "./pages/dictionary-register/dictionary-register.component";
import {HomeComponent} from "./pages/home/home.component";
import {DictionaryPageComponent} from "./pages/dictionary-page/dictionary-page.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'dictionaries', component: DictionaryRegisterComponent},
  {path: 'dictionaries/:id', component: DictionaryPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
