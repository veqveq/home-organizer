export class RecipeComp {
  id: string;
  name: string;
  hidden: boolean = false;
  groupId: string;


  constructor(id: string, name: string, group_id?: string) {
    this.id = id;
    this.name = name;
    this.groupId = group_id
  }


}
