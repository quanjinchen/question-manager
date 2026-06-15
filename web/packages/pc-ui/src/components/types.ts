export interface AccessMenuItem {
  menuName: string;
  path: string;
  icon?: string;
  children?: AccessMenuItem[];
}
