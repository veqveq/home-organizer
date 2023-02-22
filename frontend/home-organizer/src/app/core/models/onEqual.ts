export abstract class OnEqual{

  equals(obj): boolean{
    if (!obj){
      return false
    }
    return this.onEqual(obj)
  }
  protected abstract onEqual(obj): boolean;
}
