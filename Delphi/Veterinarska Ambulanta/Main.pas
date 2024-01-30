unit Main;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Error, FireDAC.UI.Intf,
  FireDAC.Phys.Intf, FireDAC.Stan.Def, FireDAC.Stan.Pool, FireDAC.Stan.Async,
  FireDAC.Phys, FireDAC.Phys.SQLite, FireDAC.Phys.SQLiteDef,
  FireDAC.Stan.ExprFuncs, FireDAC.Phys.SQLiteWrapper.Stat, FireDAC.FMXUI.Wait,
  Data.DB, FireDAC.Comp.Client;

type
  TfrmMain = class(TForm)
    FDConnection: TFDConnection;
    procedure FormCreate(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmMain: TfrmMain;
  GlobalConnection: TFDConnection;

implementation

{$R *.fmx}

procedure TfrmMain.FormCreate(Sender: TObject);
begin
GlobalConnection := TFDConnection.Create(nil);
GlobalConnection.Params.DriverID := 'SQLite';
GlobalConnection.Params.Database := 'Veterinarska_Ambulanta.db';
GlobalConnection.Connected := True;
end;

end.
