unit LoginForma;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Error, FireDAC.UI.Intf,
  FireDAC.Phys.Intf, FireDAC.Stan.Def, FireDAC.Stan.Pool, FireDAC.Stan.Async,
  FireDAC.Phys, FireDAC.Phys.FB, FireDAC.Phys.FBDef, FireDAC.FMXUI.Wait,
  Data.DB, FireDAC.Comp.Client, FMX.StdCtrls, FMX.Controls.Presentation,
  FMX.Edit, FireDAC.Stan.ExprFuncs, FireDAC.Phys.SQLiteWrapper.Stat,
  FireDAC.Phys.SQLiteDef, FireDAC.Phys.SQLite, FireDAC.Stan.Param, FMX.Layouts,
  FMX.Styles.Objects, FMX.Objects;

type
  TfrmLogIn = class(TForm)
    Sifra: TEdit;
    Sifra_Label: TLabel;
    Email: TEdit;
    Email_Label: TLabel;
    Prijava: TButton;
    Registracija: TButton;
    Zaposleni: TButton;
    Forma: TPanel;
    StyleBook: TStyleBook;
    Navbar: TImage;
    procedure RegistracijaClick(Sender: TObject);
    procedure PrijavaClick(Sender: TObject);
    procedure ZaposleniClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
    UlogovaniVlasnikID: integer;
  end;

var
  frmLogIn: TfrmLogIn;

implementation

{$R *.fmx}
uses Registracija, Vlasnik_GlavnaForma, Main, LoginZaposleni;

procedure TfrmLogIn.PrijavaClick(Sender: TObject);
var

  MyQuery: TFDQuery;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM Vlasnici WHERE Email = :Email AND Sifra = :Sifra';
    MyQuery.Params.ParamByName('Email').AsString := Email.Text;
    MyQuery.Params.ParamByName('Sifra').AsString := Sifra.Text;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
    begin
      UlogovaniVlasnikID := MyQuery.FieldByName('IDVlasnika').AsInteger;
      frmGlavniMeni.Show;
      self.hide;
    end
    else
    begin
      ShowMessage('Netačan mejl ili lozinka.');
    end;
  finally
    MyQuery.Free;
  end;
end;

procedure TfrmLogIn.RegistracijaClick(Sender: TObject);
begin
frmSignUp.show;
self.hide;
end;

procedure TfrmLogIn.ZaposleniClick(Sender: TObject);
begin
frmLogInZaposleni.show;
self.hide;
end;

end.