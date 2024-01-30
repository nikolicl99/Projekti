unit Registracija;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.StdCtrls,
  FMX.Controls.Presentation, FMX.Edit, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client, FireDAC.UI.Intf, FireDAC.Stan.Def,
  FireDAC.Stan.Pool, FireDAC.Phys, FireDAC.FMXUI.Wait, FireDAC.Phys.SQLite,
  FireDAC.Phys.SQLiteDef, FireDAC.Stan.ExprFuncs,
  FireDAC.Phys.SQLiteWrapper.Stat, FMX.Objects;
//
type
  TfrmSignUp = class(TForm)
//    EditIme: TEdit;
//    EditPrezime: TEdit;
//    EditSifra: TEdit;
//    EditTelefon: TEdit;
//    EditEmail: TEdit;
//    Ime_Label: TLabel;
//    Prezime_Label: TLabel;
//    Telefon_Label: TLabel;
//    Sifra_Label: TLabel;
//    Email_Label: TLabel;
//    Unos: TButton;
//    Login: TButton;
//    procedure UnosClick(Sender: TObject);
//    procedure LoginClick(Sender: TObject);
//    procedure FormCreate(Sender: TObject);
//  private
//    { Private declarations }
//    FEditIme: string;
//    FEditPrezime: string;
//    FEditSifra: string;
//    FEditTelefon: string;
//    FEditEmail: string;
//  public
//    { Public declarations }
//    VlasnikID:Integer;
//    property Ime: string read FEditIme write FEditIme;
//    property Prezime: string read FEditPrezime write FEditPrezime;
//    property Sifra: string read FEditSifra write FEditSifra;
//    property Telefon: string read FEditTelefon write FEditTelefon;
//    property Email: string read FEditEmail write FEditEmail;
//
//
//  end;
//
//var
//  frmSignUp: TfrmSignUp;
//
//implementation
//
//{$R *.fmx}
//uses LoginForma, Main, AdresaKorisnika;
//
//procedure TfrmSignUp.FormCreate(Sender: TObject);
//begin
//
//end;
//
//procedure TfrmSignUp.LoginClick(Sender: TObject);
//begin
//frmLogIn.show;
//self.hide;
//end;
//
//procedure TfrmSignUp.UnosClick(Sender: TObject);
//var
//  FrmAdresaKorisnika: TfrmAdresaKorisnika;
//  MyQuery:TFDQuery;
//  VlasnikID: Integer;
//begin
// FrmAdresaKorisnika := TfrmAdresaKorisnika.Create(nil);
// MyQuery := TFDQuery.Create(nil);
//  try
//FEditIme := EditIme.Text;
//  FEditPrezime := EditPrezime.Text;
//  FEditTelefon := EditTelefon.Text;
//  FEditEmail := EditEmail.Text;
//
//
//  MyQuery.SQL.Text := 'INSERT INTO Vlasnici (ImeVlasnika, PrezimeVlasnika, Telefon, Email) VALUES (:Ime, :Prezime, :BrojTelefona, :Email)';
//MyQuery.ParamByName('Ime').AsString := Ime;
//MyQuery.ParamByName('Prezime').AsString := Prezime;
//MyQuery.ParamByName('BrojTelefona').AsString := Telefon;
//MyQuery.ParamByName('Email').AsString := Email;
//MyQuery.ExecSQL;
//
//MyQuery.SQL.Text := 'SELECT LAST_INSERT_ROWID() AS LastID';
//MyQuery.Open;
//VlasnikID := MyQuery.FieldByName('LastID').AsInteger;
//MyQuery.Close;
//
//  finally
//  FrmAdresaKorisnika.ShowModal;
//             self.Hide;
//  end;
////var
////  MyQuery: TFDQuery;
////begin
////  MyQuery := TFDQuery.Create(nil);
////  try
////    MyQuery.Connection := GlobalConnection;
////    MyQuery.SQL.Text := 'INSERT INTO VLASNICI (IME, PREZIME, TELEFON, EMAIL, SIFRA) VALUES (:ime, :prezime, :telefon, :email, :sifra)';
////    MyQuery.ParamByName('ime').AsString := Ime.Text;
////    MyQuery.ParamByName('prezime').AsString := Prezime.Text;
////    MyQuery.ParamByName('telefon').AsString := Telefon.Text;
////    MyQuery.ParamByName('email').AsString := Email.Text;
////    MyQuery.ParamByName('sifra').AsString := Sifra.Text;
////    MyQuery.ExecSQL;
////
////  finally
////    MyQuery.Free;
////    self.hide;
////    frmAdresaKorisnika.Show;
////  end;
////end;
//end;
//
//end.
//unit Registracija;
//
//interface
//
//uses
//  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
//  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Controls.Presentation,
//  FMX.StdCtrls, FMX.Edit;

//type
//  TfrmRegistracija = class(TForm)
    EditIme: TEdit;
    EditPrezime: TEdit;
    EditEmail: TEdit;
    EditSifra: TEdit;
    EditTelefon: TEdit;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    procedure ButtonRegistracijaClick(Sender: TObject);
    procedure LoginClick(Sender: TObject);
  private
    { private declarations }
  public
    { public declarations }

  RegistracijaIme: string;
  RegistracijaPrezime: string;
  RegistracijaEmail: string;
  RegistracijaSifra: string;
  RegistracijaTelefon: string;
  end;
var

   frmSignUp: TfrmSignUp;

implementation

{$R *.fmx}
uses AdresaKorisnika, LoginForma;

procedure TfrmSignUp.ButtonRegistracijaClick(Sender: TObject);
begin
  RegistracijaIme := EditIme.Text;
  RegistracijaPrezime := EditPrezime.Text;
  RegistracijaEmail := EditEmail.Text;
  RegistracijaSifra := EditSifra.Text;
  RegistracijaTelefon := EditTelefon.Text;

  // Otvaranje forme za unos adresa
//  frmAdresaKorisnika := TfrmAdresaKorisnika.Create(Self);
  frmAdresaKorisnika.Show;
  self.Hide;
end;

procedure TfrmSignUp.LoginClick(Sender: TObject);
begin
frmLogin.show;
self.hide;
end;

end.

